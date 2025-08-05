package com.mandarinkafe.mandarin.core.data.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartReader {
    fun observeCartItemsCount(): Flow<Int>
    fun observeCartItems(): Flow<List<CartItem>>
}