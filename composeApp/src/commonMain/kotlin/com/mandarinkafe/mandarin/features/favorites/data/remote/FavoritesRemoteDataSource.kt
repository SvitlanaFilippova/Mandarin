package com.mandarinkafe.mandarin.features.favorites.data.remote

import com.mandarinkafe.mandarin.features.favorites.data.models.Favorites
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal

interface FavoritesRemoteDataSource {
    suspend fun getFavorites(): Favorites
    suspend fun syncFavorites(localFavorites: Set<StoredFavoriteMeal>): Favorites
}