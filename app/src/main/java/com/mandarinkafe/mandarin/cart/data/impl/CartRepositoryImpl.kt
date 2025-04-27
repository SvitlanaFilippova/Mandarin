package com.mandarinkafe.mandarin.cart.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.cart.data.mapper.CartMapper.toCartMeal
import com.mandarinkafe.mandarin.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.di.Recommends
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class CartRepositoryImpl @Inject constructor(
    private val storage: CartStorage,
    private val menuRepository: MenuRepository,
    @Recommends private val recommendsFilter: CategoryFilter,
) : CartRepository {

    override suspend fun getCart(): List<CartItem> {
        val rawCart = storage.getCart().toMutableList()

        // Ждём, пока меню загрузится
        menuRepository.menu.first { it is Resource.Success }

        val validCart = mutableListOf<CartItem>()
        val invalidIds = mutableListOf<String>()

        for (cartMeal in rawCart) {
            // Получаем по id полную актуальную информацию о блюде и передаём сохранённые добавки
            // TODO добавки тоже надо проверять на валидность!!!!!
            val fullMeal = menuRepository.getMealById(cartMeal.id)?.copy(adds = cartMeal.adds)
            if (fullMeal != null) {
                validCart.add(CartItem(meal = fullMeal, quantity = cartMeal.quantity))
            } else {
                invalidIds.add(cartMeal.id)
            }
        }

        // Удаляем все невалидные элементы из storage
        if (invalidIds.isNotEmpty()) {
            val cleanedCart = rawCart.filterNot { it.id in invalidIds }
            storage.saveCart(cleanedCart)
            Log.d("DEBUG Cart", "Удалены некорректные элементы из корзины: $invalidIds")
        }

        return validCart
    }

    override fun addToCart(meal: Meal) {
        val cart = storage.getCart().toMutableList()
        val index = cart.indexOfFirst { it.id == meal.id }

        if (index != -1) {
            val existingItem = cart[index]
            cart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            cart.add(meal.toCartMeal(quantity = 1))
        }
        storage.saveCart(cart)

    }

    override fun removeFromCart(meal: Meal) {
        val cart = storage.getCart().toMutableList()
        val index = cart.indexOfFirst { it.id == meal.id }

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

    override suspend fun getRecommends(): List<Meal> {
        // TODO Временная реализация

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