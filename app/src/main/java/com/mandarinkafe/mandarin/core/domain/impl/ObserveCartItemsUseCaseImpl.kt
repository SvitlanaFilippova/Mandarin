package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase

class ObserveCartItemsUseCaseImpl(private val reader: CartReader) : ObserveCartItemsUseCase {
    override fun invoke() = reader.observeCartItems()
}