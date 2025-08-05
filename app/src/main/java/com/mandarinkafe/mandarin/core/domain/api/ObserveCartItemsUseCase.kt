package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface ObserveCartItemsUseCase {
    operator fun invoke(): Flow<List<CartItem>>
}