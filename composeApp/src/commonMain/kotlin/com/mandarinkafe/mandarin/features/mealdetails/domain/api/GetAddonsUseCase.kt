package com.mandarinkafe.mandarin.features.mealdetails.domain.api

import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetAddonsUseCase {
    operator fun invoke(categoryPath: List<String>): Flow<Resource<List<MealAdditionalCategory>>>
}