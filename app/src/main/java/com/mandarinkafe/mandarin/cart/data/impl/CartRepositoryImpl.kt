package com.mandarinkafe.mandarin.cart.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.cart.CartMapper.toCartItem
import com.mandarinkafe.mandarin.cart.CartMapper.toStoredCartItem
import com.mandarinkafe.mandarin.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.sameAs
import com.mandarinkafe.mandarin.cart.validateBy
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.di.Recommends
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuRepository: MenuRepository,
    @Recommends private val recommendsFilter: CategoryFilter,
) : CartRepository {

    override suspend fun getCart(): Map<CartItem, Int> {

        val rawCart = storage.getCart().toMutableList()
        // Ждём, пока меню загрузится
        menuRepository.menu.first { it is Resource.Success }

        val validCart = mutableMapOf<CartItem, Int>()
        val invalidIds = mutableListOf<String>()

        for (storedCartItem in rawCart) {
            try {
                // Получаем по id полную актуальную информацию о блюде
                val fullMeal = menuRepository.getMealById(storedCartItem.mealId)
                if (fullMeal != null) {

                    val validAdds = storedCartItem.addsIds?.mapNotNull { id ->
                        menuRepository.getMealById(id)?.toMealAdditional()
                    } ?: emptyList()

                    val validModifiers =
                        storedCartItem.modifiers?.validateBy(fullMeal.modifiers) ?: emptyList()

                    val cartItem = storedCartItem.toCartItem(
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

    override fun addToCart(item: CartItem) {
        val cart = storage.getCart().toMutableList()
        cart.forEach { item ->
            Log.d("DEBUG Cart", "CartRepositoryImpl - addToCart, cart from storage: item: $item ")
        }

        val index = cart.indexOfFirst { it.sameAs(item.toStoredCartItem(0)) }

        if (index != -1) {
            val existingItem = cart[index]
            cart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            cart.add(item.toStoredCartItem(quantity = 1))
        }
        storage.saveCart(cart)
        cart.forEach { item ->
            Log.d("DEBUG Cart", "CartRepositoryImpl - addToCart, saveCart: item: $item ")
        }

    }

    override fun removeFromCart(item: CartItem) {
        val cart = storage.getCart().toMutableList()

        val index = cart.indexOfFirst { it.sameAs(item.toStoredCartItem(0)) }

        Log.d("DEBUG Cart", "CartRepositoryImpl - removeFromCart, cart from storage: item: $item ")
        if (index != -1) {
            val item = cart[index]
            if (item.quantity > 1) {
                cart[index] = item.copy(quantity = item.quantity - 1)
            } else {
                cart.removeAt(index)
            }
        }
        storage.saveCart(cart)
        cart.forEach { item ->
            Log.d("DEBUG Cart", "CartRepositoryImpl - removeFromCart, saveCart - item: $item ")
        }
    }

    override fun clearCart() {
        storage.clearCart()
    }

    override suspend fun getRecommends(): List<Meal> {
        // TODO Временная реализация, нужно будет тянуть из общего хранилища
        // и потом фильтровать в зависимости от  содержимого корзины

        val rawRecommends = mutableListOf<MealCategory>()

        menuRepository.getMenu().first { result ->
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