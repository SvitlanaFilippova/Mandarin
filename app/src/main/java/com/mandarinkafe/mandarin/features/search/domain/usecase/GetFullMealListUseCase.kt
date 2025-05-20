package com.mandarinkafe.mandarin.features.search.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import kotlinx.coroutines.flow.Flow

interface GetFullMealListUseCase {
    fun execute(): Flow<Pair<List<Meal>?, String?>>
}