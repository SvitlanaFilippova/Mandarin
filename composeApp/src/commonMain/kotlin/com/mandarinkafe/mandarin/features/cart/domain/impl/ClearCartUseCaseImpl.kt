package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter

class ClearCartUseCaseImpl(private val repository: CartWriter) : ClearCartUseCase {
    override suspend fun invoke() {
        repository.clear()
    }
}