package com.mandarinkafe.mandarin.features.favorites.data.models

fun StoredFavoriteMeal.isBase(): Boolean {
    return this.addsIds.isEmpty() && this.modifiers.isEmpty()
}

fun StoredFavoriteMeal.sameAs(other: StoredFavoriteMeal): Boolean {
    return mealId == other.mealId &&
            addsIds == other.addsIds &&
            modifiers == other.modifiers
}