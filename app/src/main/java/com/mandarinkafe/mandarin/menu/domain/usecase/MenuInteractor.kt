package com.mandarinkafe.mandarin.menu.domain.usecase

import com.mandarinkafe.mandarin.util.RVItem
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Pair<List<RVItem>?, String?>>
    fun getAddons(): Flow<Pair<List<RVItem>?, String?>>
    fun getRecommends(): Flow<Pair<List<RVItem>?, String?>>
    suspend fun refreshMenu()
}