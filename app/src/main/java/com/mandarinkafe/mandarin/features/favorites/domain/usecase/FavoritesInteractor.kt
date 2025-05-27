package com.mandarinkafe.mandarin.features.favorites.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteRecord

interface FavoritesInteractor {
    suspend fun getFavorites(): List<FavoriteRecord>
    suspend fun toggleFavorite(meal: CustomizedMeal): Boolean
    suspend fun checkIfFavorite(meal: CustomizedMeal): Boolean
}