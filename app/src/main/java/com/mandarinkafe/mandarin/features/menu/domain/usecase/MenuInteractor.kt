package com.mandarinkafe.mandarin.features.menu.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Resource<List<MealCategory>>>
    fun getAddons(): Flow<Resource<List<MealAdditionalCategory>>>
    suspend fun forceRefresh()
}