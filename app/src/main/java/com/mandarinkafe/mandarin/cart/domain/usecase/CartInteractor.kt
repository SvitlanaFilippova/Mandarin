package com.mandarinkafe.mandarin.cart.domain.usecase

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal

interface CartInteractor {
    suspend fun getCart(): List<CartItem>
    fun addToCart(meal: Meal)
    fun removeFromCart(meal: Meal)
    fun clearCart()
    suspend fun getRecommends(): List<Meal>
}