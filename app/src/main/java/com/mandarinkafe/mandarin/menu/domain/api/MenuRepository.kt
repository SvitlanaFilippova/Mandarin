package com.mandarinkafe.mandarin.menu.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MenuRepository {
    val menu: StateFlow<Resource<List<MealCategory>>>
    fun getMenu(): Flow<Resource<List<MealCategory>>>
    fun getMealById(id: String): Meal?
    suspend fun forceRefresh()
}