package com.mandarinkafe.mandarin.features.favorites.data.sharedprefs

import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal

interface FavoritesStorage {
    suspend fun toggleFavorite(meal: StoredFavoriteMeal): Boolean
    suspend fun getFavorites(): FavoritesStorageResult
    fun saveFavorites(updatedFavorites: Set<StoredFavoriteMeal>)
}