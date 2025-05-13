package com.mandarinkafe.mandarin.features.menu.domain.usecase

import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Pair<List<MenuItem>?, String?>>
    fun getAddons(): Flow<Pair<List<MealAdditionalCategory>?, String?>>
    suspend fun forceRefresh()
}