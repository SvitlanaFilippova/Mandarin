package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.util.Resource

interface CartRepository {
    suspend fun getCart(): Resource<List<CartItem>>
    fun addToCart(item: CartItem)
    fun removeFromCart(item: CartItem)
    fun clearCart()
}