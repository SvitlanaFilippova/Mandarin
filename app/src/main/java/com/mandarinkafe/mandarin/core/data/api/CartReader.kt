package com.mandarinkafe.mandarin.core.data.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartReader {
    suspend fun forceRetry()
    fun observeCartItemsCount(): Flow<Int>
    fun observeCartItems(): Flow<Resource<List<CartItem>>>
}