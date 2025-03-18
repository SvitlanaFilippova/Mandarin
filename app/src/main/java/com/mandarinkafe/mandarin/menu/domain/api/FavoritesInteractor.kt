package com.mandarinkafe.mandarin.menu.domain.api

import com.mandarinkafe.mandarin.menu.domain.models.Meal

interface FavoritesInteractor {
    fun addToFavorites(meal: Meal)
    fun removeFromFavorites(meal: Meal)
    fun checkIfFavorite(itemId: String): Boolean
}