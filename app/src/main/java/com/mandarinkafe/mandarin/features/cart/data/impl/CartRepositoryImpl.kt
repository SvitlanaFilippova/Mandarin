package com.mandarinkafe.mandarin.features.cart.data.impl

import android.annotation.SuppressLint
import android.util.Log
import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.features.cart.data.validateBy
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuCache: MenuCache,
) : CartWriter, CartReader {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var cartItems: List<CartItem> = emptyList()
    private val _cartItems = MutableStateFlow<Resource<List<CartItem>>>(Resource.Idle())
    override fun observeCartItems(): Flow<Resource<List<CartItem>>> = _cartItems.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    override fun observeCartItemsCount(): Flow<Int> = _cartCount.asStateFlow()

    init {
        getInitData()
    }

    @SuppressLint("LogNotTimber")
    private fun getInitData() {
        scope.launch {
            // 1. Получаем корзину из storage
            val storedCartItems = try {
                storage.getCartItems()
            } catch (e: Exception) {
                Log.e(ERROR_TAG, "Ошибка при чтении корзины из storage", e)
                storage.clearCart()
                _cartItems.value =
                    Resource.ErrorOther("Ошибка при чтении корзины из локального хранилища. Корзина будет очищена.")
                emptyList()
            }

            // 2. Ждём первое успешное меню, но не дольше 5 секунд
            val menuResource = withTimeoutOrNull(MENU_WAIT_TIMEOUT) {
                menuCache.allVisibleMenu
                    .filterIsInstance<Resource.Success<List<MealCategory>>>()
                    .firstOrNull()
            }

            if (menuResource == null) {
                _cartItems.value =
                    Resource.ErrorOther("Не удалось загрузить меню. Попробуйте позже.")
                return@launch
            } else {
                val fullMenu = menuResource.data.orEmpty()
                val validItems = mapAndValidate(storedCartItems, fullMenu)
                cartItems = validItems
                _cartItems.value = Resource.Success(validItems)
                _cartCount.value = validItems.sumOf { it.quantity }
            }
        }
    }

    override suspend fun forceRetry() {
        _cartItems.value = Resource.Loading()
        getInitData()
    }

    private fun mapAndValidate(
        raw: List<StoredCartItem>,
        menu: List<MealCategory>
    ): List<CartItem> {
        val valid = mutableListOf<CartItem>()

        val allMeals = flattenMeals(menu).associateBy { it.id }

        for (item in raw) {
            val baseMeal = allMeals[item.mealId]
            if (baseMeal == null) {
                continue
            }

            try {
                val adds = item.addsIds.mapNotNull { allMeals[it]?.toMealAdditional() }
                val mods = item.modifiers.validateBy(baseMeal.modifiers)
                val customizedMeal = item.toCustomizedMeal(baseMeal, adds, mods)

                valid += CartItem(
                    id = item.id,
                    customizedMeal = customizedMeal,
                    quantity = item.quantity,
                    comment = item.comment
                )
            } catch (e: Exception) {
                Log.e(ERROR_TAG, "Mapping failed for item: $item", e)
            }
        }
        return valid
    }

    private fun flattenMeals(categories: List<MealCategory>): List<Meal> {
        val result = mutableListOf<Meal>()
        fun dfs(cat: MealCategory) {
            result += cat.meals.orEmpty()
            cat.subCategories?.forEach { dfs(it) }
        }
        categories.forEach { dfs(it) }
        return result
    }

    override suspend fun addOrUpdateItem(item: CartItem): Boolean {
        var wasUpdated = false
        cartItems = cartItems.toMutableList().apply {
            val index = indexOfFirst { it.id == item.id }
            if (index != -1) {
                val oldItem = this[index]
                if (oldItem != item) {
                    this[index] = item
                    wasUpdated = true
                }
            } else {
                add(item)
            }
        }

        // Мгновенное обновление UI
        _cartItems.value = Resource.Success(cartItems)
        _cartCount.value = cartItems.sumOf { it.quantity }
        // Фоновое сохранение
        scope.launch(Dispatchers.IO) {
            storage.addOrUpdateItem(item.toStoredCartItem())

        }
        return wasUpdated
    }

    override suspend fun deleteItemById(id: String) {
        cartItems = cartItems.filterNot { it.id == id }
        _cartItems.value = Resource.Success(cartItems)
        _cartCount.value = cartItems.sumOf { it.quantity }
        scope.launch(Dispatchers.IO) {
            storage.deleteItemById(id)
        }

    }

    override suspend fun clear() {
        cartItems = emptyList()
        _cartItems.value = Resource.Success(emptyList())
        _cartCount.value = 0

        scope.launch(Dispatchers.IO) {
            storage.clearCart()
        }
    }

    companion object {
        private const val MENU_WAIT_TIMEOUT = 15000L
        private const val ERROR_TAG = "Cart DEBUG Repo"
    }
}