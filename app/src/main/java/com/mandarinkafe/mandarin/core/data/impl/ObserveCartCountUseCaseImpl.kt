package com.mandarinkafe.mandarin.core.data.impl

import com.mandarinkafe.mandarin.core.data.api.CartCountReader
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import kotlinx.coroutines.flow.Flow

class ObserveCartCountUseCaseImpl(private val reader: CartCountReader) : ObserveCartCountUseCase {
    override fun invoke(): Flow<Int> = reader.observeCartItemsCount()

}