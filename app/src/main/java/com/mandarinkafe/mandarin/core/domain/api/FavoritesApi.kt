package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal

interface FavoritesApi {
    /** Пометить блюдо избранным. */
    suspend fun addFavorite(meal: FavoriteMeal)

    /** Убрать из избранного. */
    suspend fun removeFavorite(meal: FavoriteMeal)
}