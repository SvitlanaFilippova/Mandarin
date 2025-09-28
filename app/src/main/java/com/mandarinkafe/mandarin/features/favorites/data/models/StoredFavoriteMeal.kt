package com.mandarinkafe.mandarin.features.favorites.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class StoredFavoriteMeal(
    val mealId: String,
    val timestamp: Long,
    val addsIds: List<String> = emptyList(),
    val modifiers: List<ModifierGroup> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        return other is StoredFavoriteMeal &&
                this.mealId == other.mealId &&
                this.modifiers.toSet() == other.modifiers.toSet() &&
                this.addsIds.toSet() == other.addsIds.toSet()
        // не сравниваем timestamp
    }

    override fun hashCode(): Int {
        var result = mealId.hashCode()
        result = 31 * result + addsIds.toSet().hashCode()
        result = 31 * result + modifiers.toSet().hashCode()
        return result
    }
}

fun StoredFavoriteMeal.isBase(): Boolean {
    return this.addsIds.isEmpty() && this.modifiers.isEmpty()
}

fun StoredFavoriteMeal.sameAs(other: StoredFavoriteMeal): Boolean {
    return mealId == other.mealId &&
            addsIds == other.addsIds &&
            modifiers == other.modifiers
}
