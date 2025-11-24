package com.mandarinkafe.mandarin.features.favorites.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.hasSameContentAs
import kotlinx.serialization.Serializable

@Serializable
data class StoredFavoriteMeal(
    val mealId: String,
    val createdAt: Long, // время создания записи
    val updatedAt: Long, // время последнего изменения записи
    val addsIds: List<String> = emptyList(),
    val modifiers: List<ModifierGroup> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        return other is StoredFavoriteMeal &&
                this.mealId == other.mealId &&
                this.modifiers.hasSameContentAs(other.modifiers) &&
                this.addsIds.toSet() == other.addsIds.toSet()
        // не сравниваем timestamp
    }

    override fun hashCode(): Int {
        var result = mealId.hashCode()
        result = 31 * result + addsIds.toSet().hashCode()
        // Используем только ID групп и элементов для hashCode
        result = 31 * result + modifiers.map { it.id to it.items.map { item -> item.id }.toSet() }.hashCode()
        return result
    }
}

fun StoredFavoriteMeal.isBase(): Boolean {
    return this.addsIds.isEmpty() && this.modifiers.isEmpty()
}

fun StoredFavoriteMeal.sameAs(other: StoredFavoriteMeal): Boolean {
    return mealId == other.mealId &&
            addsIds.toSet() == other.addsIds.toSet() &&
            modifiers.hasSameContentAs(other.modifiers)
}

