package com.mandarinkafe.mandarin.shared.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.shared.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.shared.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.util.Resource

class CartInteractorImpl(private val repository: CartRepository) : CartInteractor {
    override suspend fun getCart(): Resource<Map<CustomizedMeal, Int>> {

        return repository.getCart()
    }

    override fun addToCart(item: CustomizedMeal) {
        repository.addToCart(item)
    }

    override fun removeFromCart(item: CustomizedMeal) {
        repository.removeFromCart(item)
    }

    override fun clearCart() {
        repository.clearCart()
    }
}