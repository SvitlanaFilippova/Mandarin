package com.mandarinkafe.mandarin.menu.data

import com.mandarinkafe.mandarin.menu.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.domain.models.Meal

class FavoritesRepositoryImpl(private val localStorage: LocalStorage) : FavoritesRepository {
    override fun addToFavorites(meal: Meal) {
        localStorage.addToFavorites(meal.id)
    }

    override fun removeFromFavorites(meal: Meal) {
        localStorage.removeFromFavorites(meal.id)
    }
    override fun getFavoriteIds() : List<String> {
        return localStorage.getSavedFavorites().toList()
    }


    override fun checkIfFavorite(itemId: String): Boolean {
        return getFavoriteIds().contains(itemId)
    }
}
