package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface ObserveCartItemsUseCase {
    operator fun invoke(): Flow<Resource<List<CartItem>>>
}