package com.mandarinkafe.mandarin.favorites.data.sharedprefs

import com.mandarinkafe.mandarin.favorites.domain.models.FavoriteMeal

interface LocalStorage {

    fun getFavorites(): Set<FavoriteMeal>
    fun addToFavorites(meal: FavoriteMeal)
    fun removeFromFavorites(mealId: String)
}