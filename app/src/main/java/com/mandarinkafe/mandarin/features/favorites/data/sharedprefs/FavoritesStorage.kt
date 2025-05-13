package com.mandarinkafe.mandarin.features.favorites.data.sharedprefs

import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal

interface FavoritesStorage {

    fun getFavorites(): Set<FavoriteMeal>
    fun addToFavorites(meal: FavoriteMeal)
    fun removeFromFavorites(mealId: String)
}