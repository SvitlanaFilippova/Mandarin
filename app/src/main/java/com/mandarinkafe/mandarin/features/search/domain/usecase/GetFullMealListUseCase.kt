package com.mandarinkafe.mandarin.features.search.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import kotlinx.coroutines.flow.Flow

interface GetFullMealListUseCase {
    operator fun invoke(): Flow<Pair<List<Meal>?, String?>>
}