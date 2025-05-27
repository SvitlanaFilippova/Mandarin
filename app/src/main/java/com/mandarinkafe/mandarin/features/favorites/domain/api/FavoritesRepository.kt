package com.mandarinkafe.mandarin.features.favorites.domain.api

import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteRecord

interface FavoritesRepository {
    suspend fun toggleFavorite(item: FavoriteRecord): Boolean
    suspend fun getFavorites(): Set<FavoriteRecord>
    suspend fun checkIfFavorite(item: FavoriteRecord): Boolean
}