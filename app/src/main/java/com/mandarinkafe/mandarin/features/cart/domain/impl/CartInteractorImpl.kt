package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor

class CartInteractorImpl(private val repository: CartRepository) : CartInteractor {
    override suspend fun getCart(): Map<CartItem, Int> {
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

    override suspend fun getRecommends(): List<Meal> {
        return repository.getRecommends()
    }
}