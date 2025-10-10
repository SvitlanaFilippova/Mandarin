package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import kotlinx.coroutines.flow.Flow

class ObserveCartCountUseCaseImpl(private val reader: CartReader) : ObserveCartCountUseCase {
    override fun invoke(): Flow<Int> = reader.observeCartItemsCount()

}