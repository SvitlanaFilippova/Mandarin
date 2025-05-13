package com.mandarinkafe.mandarin.features.favorites.domain.usecase

import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal

interface FavoritesInteractor {
    fun getFavorites(): List<FavoriteMeal>
    fun addToFavorites(meal: FavoriteMeal)
    fun removeFromFavorites(meal: FavoriteMeal)
    fun checkIfFavorite(itemId: String): Boolean
}