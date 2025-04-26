package com.mandarinkafe.mandarin.cart.domain.impl

import com.mandarinkafe.mandarin.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.core.domain.models.Meal

class CartInteractorImpl(private val repository: CartRepository) : CartInteractor {
    override suspend fun getCart(): List<CartItem> {
        return repository.getCart()
    }

    override fun addToCart(meal: Meal) {
        repository.addToCart(meal)
    }

    override fun removeFromCart(meal: Meal) {
        repository.removeFromCart(meal)
    }

    override fun clearCart() {
        repository.clearCart()
    }
}