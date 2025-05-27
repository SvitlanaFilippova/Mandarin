package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord

interface FavoritesWriter {
    /** Добавляет или убирает запись; возвращает новое состояние (true = теперь в избранном). */
    suspend fun toggleFavorite(record: FavoriteRecord): Boolean

    /** Полностью перезаписать все записи в storage. */
    suspend fun saveFavorites(records: Set<FavoriteRecord>)
}