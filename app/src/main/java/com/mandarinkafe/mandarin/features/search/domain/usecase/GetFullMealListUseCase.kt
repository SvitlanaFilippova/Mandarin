package com.mandarinkafe.mandarin.features.search.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface GetFullMealListUseCase {
    operator fun invoke(): Flow<Resource<List<Meal>>>
}