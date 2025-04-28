package com.mandarinkafe.mandarin.menu.domain.usecase

import com.mandarinkafe.mandarin.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Pair<List<MenuItem>?, String?>>
    fun getAddons(): Flow<Pair<List<MealAdditionalCategory>?, String?>>
    suspend fun forceRefresh()
}