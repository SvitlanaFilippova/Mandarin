package com.mandarinkafe.mandarin.features.favorites.data.datastore

import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal

interface FavoritesStorage {
    suspend fun toggleFavorite(meal: StoredFavoriteMeal): Boolean
    suspend fun getFavorites(): FavoritesStorageResult
    suspend fun saveFavorites(updatedFavorites: Set<StoredFavoriteMeal>)
}

