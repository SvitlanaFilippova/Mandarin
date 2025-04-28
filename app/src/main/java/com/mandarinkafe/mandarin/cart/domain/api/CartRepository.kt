package com.mandarinkafe.mandarin.cart.domain.api

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal

interface CartRepository {
    suspend fun getCart(): List<CartItem>
    fun addToCart(meal: Meal)
    fun removeFromCart(meal: Meal)
    fun clearCart()
}