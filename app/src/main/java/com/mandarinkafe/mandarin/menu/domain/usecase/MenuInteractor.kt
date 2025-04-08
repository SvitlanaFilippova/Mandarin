package com.mandarinkafe.mandarin.menu.domain.usecase

import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import kotlinx.coroutines.flow.Flow

interface MenuInteractor {
    fun getMenu(): Flow<Pair<List<MenuRVItem>?, String?>>
    fun getAddons(): Flow<Pair<List<MenuRVItem>?, String?>>
    fun getRecommends(): Flow<Pair<List<MenuRVItem>?, String?>>
    suspend fun forceRefresh()
}