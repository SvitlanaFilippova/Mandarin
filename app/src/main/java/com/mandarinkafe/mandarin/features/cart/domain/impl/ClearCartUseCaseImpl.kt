package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.features.cart.domain.api.CartRepository

class ClearCartUseCaseImpl(private val repository: CartRepository) : ClearCartUseCase {
    override fun invoke() {
        repository.clearCart()
    }
}