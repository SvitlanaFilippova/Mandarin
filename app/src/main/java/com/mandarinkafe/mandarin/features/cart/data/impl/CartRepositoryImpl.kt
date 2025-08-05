package com.mandarinkafe.mandarin.features.cart.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.models.sameAs
import com.mandarinkafe.mandarin.features.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuCache: MenuCache,
) : CartRepository, CartReader {

    private var rawCart: List<StoredCartItem>? = null
    private val _cartCount = MutableStateFlow(0)
    override fun observeCartItemsCount(): Flow<Int> = _cartCount.asStateFlow()

    private val _cartItems = MutableStateFlow<Map<CustomizedMeal, Int>>(emptyMap())
    override fun observeCartItems(): Flow<Map<CustomizedMeal, Int>> = _cartItems.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshCart(storage.getCart())
        }
    }

    override suspend fun getCart(): Resource<Map<CustomizedMeal, Int>> {
        // 1) дождёмся меню (или сразу вернём его ошибку)
        val menuResult = awaitMenu()
        if (menuResult !is Resource.Success) {
            return when (menuResult) {
                is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
                is Resource.ErrorOther -> Resource.ErrorOther(menuResult.message.orEmpty())
                else -> Resource.ErrorOther(ERROR_UNEXPECTED_STATE)
            }
        }
        val categories = menuResult.data.orEmpty()

        // 2) загрузим сырую корзину (или ErrorOther при исключении)
        val loadedRawCart = rawCart ?: loadRawCart()
        if (loadedRawCart == null) return Resource.ErrorOther(ERROR_CART_READ)

        // 3) замапим и проверим каждый элемент
        val (validCart, invalidIds) = mapAndValidate(loadedRawCart, categories)

        // 4) почистим storage от невалидных
        cleanupInvalid(invalidIds, loadedRawCart)

        // 5) вернём результат
        return if (validCart.isEmpty()) {
            Resource.ErrorEmptyData()
        } else {
            Resource.Success(validCart)
        }
    }

    private suspend fun awaitMenu(): Resource<List<MealCategory>> {
        // ждём первого финального состояния
        val final = menuCache.fullMenu
            .filter { it !is Resource.Loading && it !is Resource.Idle }
            .first()

        return when (final) {
            is Resource.Success -> Resource.Success(final.data.orEmpty())
            is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
            is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
            is Resource.ErrorOther -> Resource.ErrorOther(final.message.orEmpty())
            else -> Resource.ErrorOther(ERROR_UNKNOWN_MENU_STATE)
        }
    }

    private fun mapAndValidate(
        raw: List<StoredCartItem>,
        menu: List<MealCategory>
    ): Pair<Map<CustomizedMeal, Int>, List<String>> {
        val valid = mutableMapOf<CustomizedMeal, Int>()
        val invalid = mutableListOf<String>()

        val allMeals = menu
            .flatMap { cat ->
                cat.meals.orEmpty() + cat.subCategories.orEmpty().flatMap { it.meals.orEmpty() }
            }
            .associateBy { it.id }

        raw.forEach { item ->
            val meal = allMeals[item.mealId]
            if (meal == null) {
                invalid += item.mealId
                return@forEach
            }
            try {
                val adds = item.addsIds
                    ?.mapNotNull { allMeals[it]?.toMealAdditional() }
                    .orEmpty()
                val mods = item.modifiers
                    ?.validateBy(meal.modifiers)
                    .orEmpty()

                val cm = item.toCustomizedMeal(meal, adds, mods)
                Log.d(ERROR_TAG, "Mapped to CustomizedMeal: $cm")
                valid[cm] = item.quantity
            } catch (e: Exception) {
                Log.e(ERROR_TAG, "Mapping failed for item: $item", e)
                invalid += item.mealId
            }
        }
        return valid to invalid
    }

    private fun cleanupInvalid(invalid: List<String>, raw: List<StoredCartItem>) {
        if (invalid.isEmpty()) return
        val cleaned = raw.filterNot { it.mealId in invalid }
        storage.saveCart(cleaned)
        Log.d(ERROR_TAG, "Removed invalid items: $invalid")
    }

    override fun addToCart(item: CustomizedMeal) {
        val cart = storage.getCart().toMutableList()
        Log.d(ERROR_TAG, "Before add: $cart")
        val index = cart.indexOfFirst { it.sameAs(item.toStoredCartItem(0)) }

        if (index != -1) {
            val existingItem = cart[index]
            cart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            cart.add(item.toStoredCartItem(quantity = 1))
        }
        Log.d(ERROR_TAG, "Saving cart: $cart")
        storage.saveCart(cart)
        CoroutineScope(Dispatchers.IO).launch {
            refreshCart(cart)
        }
        Log.d(ERROR_TAG, "After add: $cart")
    }

    override fun removeFromCart(item: CustomizedMeal) {
        val cart = storage.getCart().toMutableList()
        val index = cart.indexOfFirst { it.sameAs(item.toStoredCartItem(0)) }

        if (index != -1) {
            val item = cart[index]
            if (item.quantity > 1) {
                cart[index] = item.copy(quantity = item.quantity - 1)
            } else {
                cart.removeAt(index)
            }
        }
        storage.saveCart(cart)
        CoroutineScope(Dispatchers.IO).launch {
            refreshCart(cart)
        }
    }

    override fun clearCart() {
        storage.clearCart()
        rawCart = null
        CoroutineScope(Dispatchers.IO).launch {
            refreshCart(emptyList())
        }
    }

    private fun loadRawCart(): List<StoredCartItem>? {
        return try {
            storage.getCart().also { rawCart = it }
        } catch (e: Exception) {
            Log.e(ERROR_TAG, "loadRawCart failed", e)
            null
        }
    }

    private suspend fun refreshCart(updatedRawCart: List<StoredCartItem>) {
        rawCart = updatedRawCart

        val menu = awaitMenu()
        if (menu !is Resource.Success) {
            _cartItems.value = emptyMap()
            _cartCount.value = 0
            return
        }

        val (valid, invalidIds) = mapAndValidate(updatedRawCart, menu.data ?: emptyList())
        cleanupInvalid(invalidIds, updatedRawCart)

        _cartItems.value = valid
        _cartCount.value = valid.values.sum()
    }


    companion object {
        private const val ERROR_TAG = "CartRepository"
        private const val ERROR_CART_READ = "Ошибка чтения корзины"
        private const val ERROR_UNEXPECTED_STATE = "Unexpected state"
        private const val ERROR_UNKNOWN_MENU_STATE = "Unknown menu state"
    }
}