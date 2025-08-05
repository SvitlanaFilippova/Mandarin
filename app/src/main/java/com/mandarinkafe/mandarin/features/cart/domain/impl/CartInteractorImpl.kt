package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.util.Resource

class CartInteractorImpl(private val repository: CartRepository) : CartInteractor {
    override suspend fun getCart(): Resource<List<CartItem>> {
        return repository.getCart()
    }

    override fun addToCart(item: CartItem) {
        repository.addToCart(item)
    }

    override fun removeFromCart(item: CartItem) {
        repository.removeFromCart(item)
    }

    override fun clearCart() {
        repository.clearCart()
    }
}