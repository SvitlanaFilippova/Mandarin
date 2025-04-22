package com.mandarinkafe.mandarin.menu.domain.usecase

import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Pair<List<MenuItem>?, String?>>
    fun getAddons(): Flow<Pair<List<MealCategory>?, String?>>
    fun getRecommends(): Flow<Pair<List<MenuItem>?, String?>>
    suspend fun forceRefresh()
}