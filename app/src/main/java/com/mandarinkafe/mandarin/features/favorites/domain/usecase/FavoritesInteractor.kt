package com.mandarinkafe.mandarin.features.favorites.domain.usecase

import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal

interface FavoritesInteractor {
    suspend fun getFavorites(): List<FavoriteMeal>
    suspend fun addFavorite(meal: FavoriteMeal)
    suspend fun removeFavorite(meal: FavoriteMeal)
    suspend fun checkIfFavorite(itemId: String): Boolean
}