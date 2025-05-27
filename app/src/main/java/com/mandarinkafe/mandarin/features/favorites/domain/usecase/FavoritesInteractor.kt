package com.mandarinkafe.mandarin.features.favorites.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord

interface FavoritesInteractor {
    suspend fun getFavorites(): List<FavoriteRecord>
    suspend fun toggleFavorite(item: FavoriteRecord): Boolean
    suspend fun checkIfFavorite(item: FavoriteRecord): Boolean
}