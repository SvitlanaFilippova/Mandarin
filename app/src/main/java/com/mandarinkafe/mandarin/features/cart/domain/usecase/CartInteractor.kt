package com.mandarinkafe.mandarin.features.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartInteractor {
    fun observeCartItemsCount(): Flow<Int>
    fun observeCartItems(): Flow<Resource<List<CartItem>>>

    suspend fun addItem(
        cartItem: CartItem? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null
    )

    suspend fun updateItem(cartItem: CartItem)

    suspend fun removeFromCart(
        cartItem: CartItem? = null,
        customizedMeal: CustomizedMeal? = null,
        meal: Meal? = null
    )

    suspend fun clearCart()

}