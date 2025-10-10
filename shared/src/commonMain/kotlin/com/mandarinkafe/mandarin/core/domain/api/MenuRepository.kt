package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    suspend fun getMeals(): Flow<List<Meal>>
    suspend fun getMealsByCategory(category: String): Flow<List<Meal>>
}


