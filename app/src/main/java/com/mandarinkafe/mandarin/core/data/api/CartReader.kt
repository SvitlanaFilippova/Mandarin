package com.mandarinkafe.mandarin.core.data.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import kotlinx.coroutines.flow.Flow

interface CartReader {
    fun observeCartItemsCount(): Flow<Int>
    fun observeCartItems(): Flow<Map<CustomizedMeal, Int>>
}