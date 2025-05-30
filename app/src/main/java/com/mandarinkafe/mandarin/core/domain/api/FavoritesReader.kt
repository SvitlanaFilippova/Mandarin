package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesReader {
    /** Возвращает «сырые» записи из storage (без валидации). */
    suspend fun getRawFavorites(): Resource<Set<FavoriteRecord>>

    /** Возвращает ID только "базовых" блюд из storage. */
    fun getBaseFavoritesIds(): Set<String>

    /** Проверяет, добавлена ли item в список избранных в storage */
    suspend fun checkIfFavorite(item: FavoriteRecord): Boolean

    fun observeRawFavorites(): Flow<Resource<Set<FavoriteRecord>>>
    fun observeBaseFavoritesIds(): Flow<Set<String>>

}