package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.models.MealAddResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartInteractor {
    fun observeCartItemsCount(): Flow<Int>
    fun observeCartItems(): Flow<Resource<List<CartItem>>>
    suspend fun forceRefresh()
    suspend fun syncWithRemote()

    suspend fun addItem(
        cartItem: CartItem? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null,
    )

    /**
     * если передан только newCartItem, то обновляет его. Если передан oldItem - заменяет его новым.
    Возвращает true, если позиция действительно была изменена и false, если изменений не было (newCartItem оказался со контенту таким же, как oldItem)
     */
    suspend fun updateItem(newCartItem: CartItem, oldItem: CartItem? = null): Boolean

    /**
    Проверяет, есть ли в корзине уже такое блюдо.
    Если нет - просто добавляет в корзину переданный cartItem.
     */
    suspend fun tryAddMeal(cartItem: CartItem): MealAddResult

    suspend fun removeFromCart(
        cartItemId: String? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null,
    )

    suspend fun clearCart()
}