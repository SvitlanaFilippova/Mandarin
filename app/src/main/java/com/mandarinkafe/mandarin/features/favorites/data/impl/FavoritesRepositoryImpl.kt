package com.mandarinkafe.mandarin.features.favorites.data.impl

import com.mandarinkafe.mandarin.core.data.api.FavoritesReader
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal

class FavoritesRepositoryImpl(private val favoritesStorage: FavoritesStorage) :
    FavoritesRepository, FavoritesReader {
    override suspend fun addFavorite(meal: FavoriteMeal) {
        favoritesStorage.addToFavorites(meal)
    }

    override suspend fun removeFavorite(meal: FavoriteMeal) {
        favoritesStorage.removeFromFavorites(meal.id)
    }

    override suspend fun getFavoritesIds(): Set<String> {
        return favoritesStorage.getFavorites().map { it.id }.toSet()
    }

    override suspend fun getFavorites(): List<FavoriteMeal> {
        return favoritesStorage.getFavorites().toList()
    }

    override suspend fun checkIfFavorite(itemId: String): Boolean {
        return getFavorites().any { it.id == itemId }
    }
}