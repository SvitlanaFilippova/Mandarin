package com.mandarinkafe.mandarin.features.cart.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.features.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.features.cart.sameAs
import com.mandarinkafe.mandarin.features.cart.validateBy
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.di.Recommends
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuCache: MenuCache,
    @Recommends private val recommendsFilter: CategoryFilter,
) : CartRepository {

    override suspend fun getCart(): Map<CustomizedMeal, Int> {

        val rawCart = storage.getCart()
        // Ждём, пока меню загрузится
        menuCache.menu.first { it is Resource.Success }

        val validCart = mutableMapOf<CustomizedMeal, Int>()
        val invalidIds = mutableListOf<String>()

        for (storedCartItem in rawCart) {
            try {
                // Получаем по id полную актуальную информацию о блюде
                val fullMeal = menuCache.getMealById(storedCartItem.mealId)
                if (fullMeal != null) {

                    val validAdds = storedCartItem.addsIds?.mapNotNull { id ->
                        menuCache.getMealById(id)?.toMealAdditional()
                    } ?: emptyList()

                    val validModifiers =
                        storedCartItem.modifiers?.validateBy(fullMeal.modifiers) ?: emptyList()

                    val cartItem = storedCartItem.toCustomizedMeal(
                        meal = fullMeal,
                        adds = validAdds,
                        modifiers = validModifiers
                    )
                    validCart[cartItem] = storedCartItem.quantity
                } else {
                    invalidIds.add(storedCartItem.mealId)
                }
            } catch (e: Exception) {
                // Если при преобразовании или доступе к данным что-то пошло не так — тоже игнорируем
                Log.e(
                    "CartMapper",
                    "Ошибка при преобразовании StoredCartItem с id ${storedCartItem.mealId}",
                )
                invalidIds.add(storedCartItem.mealId)
            }
        }

        // Удаляем все невалидные элементы из storage
        if (invalidIds.isNotEmpty()) {
            val cleanedCart = rawCart.filterNot { it.mealId in invalidIds }
            storage.saveCart(cleanedCart)
            Log.d("DEBUG Cart", "Удалены некорректные элементы из корзины: $invalidIds")
        }

        return validCart
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

    override suspend fun getCommonRecommends(): List<Meal> {
        val rawRecommends = mutableListOf<MealCategory>()
        menuCache.menu.first { result ->
            if (result is Resource.Success) {
                val filtered = result.data?.filter { recommendsFilter.isMatch(it) }.orEmpty()
                rawRecommends.addAll(filtered)
                true // чтобы завершить first()
            } else {
                false
            }
        }
        return rawRecommends.flatMap {
            it.meals.orEmpty()
        }
    }

}