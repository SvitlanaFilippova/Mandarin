package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetInitialDataUseCase {
    suspend operator fun invoke(): Flow<Resource<List<MealCategory>>>
}