package com.mandarinkafe.mandarin.favorites.domain.api

import com.mandarinkafe.mandarin.favorites.domain.models.FavoriteMeal

interface FavoritesRepository {
    fun addToFavorites(meal: FavoriteMeal)
    fun removeFromFavorites(meal: FavoriteMeal)
    fun getFavorites(): List<FavoriteMeal>
    fun checkIfFavorite(itemId: String): Boolean

}