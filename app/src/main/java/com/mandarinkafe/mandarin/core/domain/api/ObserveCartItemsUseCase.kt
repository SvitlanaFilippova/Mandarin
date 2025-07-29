package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import kotlinx.coroutines.flow.Flow

interface ObserveCartItemsUseCase {
    operator fun invoke(): Flow<Map<CustomizedMeal, Int>>
}