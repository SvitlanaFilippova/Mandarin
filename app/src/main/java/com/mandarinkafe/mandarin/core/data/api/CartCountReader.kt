package com.mandarinkafe.mandarin.core.data.api

import kotlinx.coroutines.flow.Flow

interface CartCountReader {
    fun observeCartItemsCount(): Flow<Int>
}