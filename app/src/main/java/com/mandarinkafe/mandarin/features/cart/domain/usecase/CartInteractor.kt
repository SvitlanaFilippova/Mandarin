package com.mandarinkafe.mandarin.features.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.util.Resource

interface CartInteractor {
    suspend fun getCart(): Resource<List<CartItem>>
    fun addToCart(item: CartItem)
    fun removeFromCart(item: CartItem)
    fun clearCart()
}