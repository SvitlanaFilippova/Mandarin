package com.mandarinkafe.mandarin.features.favorites.data.datastore

import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal

sealed class FavoritesStorageResult {
    data class Success(val favorites: Set<StoredFavoriteMeal>) : FavoritesStorageResult()
    data class Corrupted(val cleared: Boolean = true) : FavoritesStorageResult()
}





