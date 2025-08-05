package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

class ObserveCartItemsUseCaseImpl(private val reader: CartReader) : ObserveCartItemsUseCase {
    override fun invoke(): Flow<List<CartItem>> = reader.observeCartItems()
}