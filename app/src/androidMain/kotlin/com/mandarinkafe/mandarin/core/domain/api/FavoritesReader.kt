package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesReader {
    /** Возвращает ID только "базовых" блюд из storage. */
    fun getBaseFavoritesIds(): Set<String>
    fun observeBaseFavoritesIds(): Flow<Set<String>>

    fun observeFavorites(): Flow<Resource<List<CustomizedMeal>>>

    suspend fun forceRetry()
}