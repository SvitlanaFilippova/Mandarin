package com.mandarinkafe.mandarin.menu.domain.api

import com.mandarinkafe.mandarin.menu.domain.models.Meal

interface FavoritesRepository {
    fun addToFavorites(meal: Meal)
    fun removeFromFavorites(meal: Meal)
    fun getFavoriteIds() : List<String>
    fun checkIfFavorite(itemId: String): Boolean

}


