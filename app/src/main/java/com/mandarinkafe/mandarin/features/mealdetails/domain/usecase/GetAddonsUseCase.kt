package com.mandarinkafe.mandarin.features.mealdetails.domain.usecase

import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetAddonsUseCase {
    operator fun invoke(): Flow<Resource<List<MealAdditionalCategory>>>
}