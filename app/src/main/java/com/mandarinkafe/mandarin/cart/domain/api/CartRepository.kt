package com.mandarinkafe.mandarin.cart.domain.api

import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal

interface CartRepository {
    suspend fun getCart(): Map<CartItem, Int>
    fun addToCart(item: CartItem)
    fun removeFromCart(item: CartItem)
    fun clearCart()
    suspend fun getRecommends(): List<Meal>
}