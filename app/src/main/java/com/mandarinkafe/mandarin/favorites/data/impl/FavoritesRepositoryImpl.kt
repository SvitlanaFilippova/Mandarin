package com.mandarinkafe.mandarin.favorites.data.impl

import com.mandarinkafe.mandarin.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.favorites.domain.models.FavoriteMeal

class FavoritesRepositoryImpl(private val favoritesStorage: FavoritesStorage) :
    FavoritesRepository {
    override fun addToFavorites(meal: FavoriteMeal) {
        favoritesStorage.addToFavorites(meal)
    }

    override fun removeFromFavorites(meal: FavoriteMeal) {
        favoritesStorage.removeFromFavorites(meal.id)
    }

    override fun getFavorites(): List<FavoriteMeal> {
        return favoritesStorage.getFavorites().toList()
    }

    override fun checkIfFavorite(itemId: String): Boolean {
        return getFavorites().any { it.id == itemId }
    }
}