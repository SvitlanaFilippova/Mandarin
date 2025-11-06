package com.mandarinkafe.mandarin.features.favorites.data.remote

import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal

interface FavoritesRemoteDataSource {
    suspend fun getFavorites(): Set<StoredFavoriteMeal>
    suspend fun syncFavorites(localFavorites: Set<FavoriteRecord>)
}