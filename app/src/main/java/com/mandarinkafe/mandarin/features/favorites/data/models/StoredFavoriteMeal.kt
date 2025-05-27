package com.mandarinkafe.mandarin.features.favorites.data.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

data class StoredFavoriteMeal(
    val mealId: String,
    val timestamp: Long,
    val addsIds: List<String> = emptyList(),
    val modifiers: List<ModifierGroup> = emptyList()
)
