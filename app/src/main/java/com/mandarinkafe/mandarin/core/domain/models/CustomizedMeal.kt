package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable

@Stable
data class CustomizedMeal(
    val meal: Meal,

    /**
    Выбранные добавки для пиццы
     */
    val adds: List<MealAdditional> = emptyList<MealAdditional>(),

    /**
    Выбранные модификаторы (по группам)
     */
    val modifiers: List<ModifierGroup> = emptyList<ModifierGroup>()
)