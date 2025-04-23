package com.mandarinkafe.mandarin.favorites.data.impl

import com.mandarinkafe.mandarin.favorites.data.sharedprefs.LocalStorage
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.favorites.domain.models.FavoriteMeal

class FavoritesRepositoryImpl(private val localStorage: LocalStorage) : FavoritesRepository {
    override fun addToFavorites(meal: FavoriteMeal) {
        localStorage.addToFavorites(meal)
    }

    override fun removeFromFavorites(meal: FavoriteMeal) {
        localStorage.removeFromFavorites(meal.id)
    }

    override fun getFavorites(): List<FavoriteMeal> {
        return localStorage.getFavorites().toList()
    }

    override fun checkIfFavorite(itemId: String): Boolean {
        return getFavorites().any { it.id == itemId }
    }
}