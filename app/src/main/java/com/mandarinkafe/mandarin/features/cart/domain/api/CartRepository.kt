package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem

interface CartRepository {
    suspend fun getCart(): Map<CartItem, Int>
    fun addToCart(item: CartItem)
    fun removeFromCart(item: CartItem)
    fun clearCart()
    suspend fun getRecommends(): List<Meal>
}