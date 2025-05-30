package com.mandarinkafe.mandarin.shared.cart.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.shared.cart.data.models.StoredCartItem
import com.mandarinkafe.mandarin.shared.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.shared.cart.domain.CartMapper.toCustomizedMeal
import com.mandarinkafe.mandarin.shared.cart.domain.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.shared.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.shared.cart.sameAs
import com.mandarinkafe.mandarin.shared.cart.validateBy
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuCache: MenuCache,
) : CartRepository {

    override suspend fun getCart(): Resource<Map<CustomizedMeal, Int>> {
        // 1) дождёмся меню (или сразу вернём его ошибку)
        val menuResult = awaitMenu()
        if (menuResult !is Resource.Success) {
            Log.d("DEBUG EMPTY CART", "menuResult !is Resource.Success")
            // Resource.ErrorNoInternet, ErrorEmptyData или ErrorOther
            return when (menuResult) {
                is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
                is Resource.ErrorOther -> Resource.ErrorOther(menuResult.message.orEmpty())
                else -> Resource.ErrorOther("Unexpected state")
            }
        }
        val categories = menuResult.data.orEmpty()

        // 2) загрузим сырую корзину (или ErrorOther при исключении)
        val rawCart = loadRawCart()
            ?: return Resource.ErrorOther("Ошибка чтения корзины")

        // 3) замапим и проверим каждый элемент
        val (validCart, invalidIds) = mapAndValidate(rawCart, categories)

        // 4) почистим storage от невалидных
        cleanupInvalid(invalidIds, rawCart)

        // 5) вернём результат
        return if (validCart.isEmpty()) {
            Log.d("DEBUG EMPTY CART", "CartRepositoryImpl. validCart is Empty")
            Resource.ErrorEmptyData()
        } else {
            Resource.Success(validCart)
        }
    }

    private suspend fun awaitMenu(): Resource<List<MealCategory>> {
        // ждём первого финального состояния
        val final = menuCache.menu
            .filter { it !is Resource.Loading && it !is Resource.Idle }
            .first()

        return when (final) {
            is Resource.Success -> Resource.Success(final.data.orEmpty())
            is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
            is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
            is Resource.ErrorOther -> Resource.ErrorOther(final.message.orEmpty())
            else -> Resource.ErrorOther("Unknown menu state")
        }
    }

    private fun loadRawCart(): List<StoredCartItem>? = try {
        storage.getCart()
    } catch (e: Exception) {
        Log.e("GetCartUseCase", "loadRawCart failed", e)
        null
    }

    private fun mapAndValidate(
        raw: List<StoredCartItem>,
        menu: List<MealCategory>
    ): Pair<Map<CustomizedMeal, Int>, List<String>> {
        val valid = mutableMapOf<CustomizedMeal, Int>()
        val invalid = mutableListOf<String>()

        // ускорим lookup по id
        val allMeals = menu
            .flatMap { cat ->
                (cat.meals.orEmpty() + cat.subCategories.orEmpty().flatMap { it.meals.orEmpty() })
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
                valid[cm] = item.quantity
            } catch (e: Exception) {
                Log.e("GetCartUseCase", "map failed for ${item.mealId}", e)
                invalid += item.mealId
            }
        }
        return valid to invalid
    }

    private fun cleanupInvalid(invalid: List<String>, raw: List<StoredCartItem>) {
        if (invalid.isEmpty()) return
        val cleaned = raw.filterNot { it.mealId in invalid }
        storage.saveCart(cleaned)
        Log.d("GetCartUseCase", "Removed invalid items: $invalid")
    }

    override fun addToCart(item: CustomizedMeal) {
        val cart = storage.getCart().toMutableList()
        val index = cart.indexOfFirst { it.sameAs(item.toStoredCartItem(0)) }

        if (index != -1) {
            val existingItem = cart[index]
            cart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            cart.add(item.toStoredCartItem(quantity = 1))
        }
        storage.saveCart(cart)

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
    }

    override fun clearCart() {
        storage.clearCart()
    }

}