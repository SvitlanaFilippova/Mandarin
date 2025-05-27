package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal

interface FavoritesApi {
    suspend fun toggleFavorite(id: String): Boolean
    suspend fun toggleFavorite(meal: CustomizedMeal): Boolean
}