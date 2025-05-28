package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord

interface FavoritesReader {
    /** Возвращает «сырые» записи из storage (без валидации). */
    suspend fun getRawFavorites(): Set<FavoriteRecord>

    /** Возвращает ID только "базовых" блюд из storage. */
    suspend fun getBaseFavoritesIds(): Set<String>

    /** Проверяет, добавлена ли item в список избранных в storage */
    suspend fun checkIfFavorite(item: FavoriteRecord): Boolean
}