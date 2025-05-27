package com.mandarinkafe.mandarin.features.favorites.domain.api

import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal

interface FavoritesRepository {
    suspend fun addFavorite(meal: FavoriteMeal)
    suspend fun removeFavorite(meal: FavoriteMeal)
    suspend fun getFavoritesIds(): Set<String>
    suspend fun getFavorites(): List<FavoriteMeal>
    suspend fun checkIfFavorite(itemId: String): Boolean
}