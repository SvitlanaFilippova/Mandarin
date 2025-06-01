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
                this.modifiers == other.modifiers &&
                this.addsIds == other.addsIds
        // не сравниваем timestamp
    }

    override fun hashCode(): Int {
        return 31 * mealId.hashCode() + modifiers.hashCode()
    }
}
