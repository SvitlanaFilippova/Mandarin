package com.mandarinkafe.mandarin.features.menu.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Pair<List<MealCategory>?, String?>>
    fun getAddons(): Flow<Pair<List<MealAdditionalCategory>?, String?>>
    suspend fun forceRefresh()
}