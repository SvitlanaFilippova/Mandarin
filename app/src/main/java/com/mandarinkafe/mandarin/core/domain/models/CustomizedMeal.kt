package com.mandarinkafe.mandarin.core.domain.models

data class CustomizedMeal(
    val meal: Meal,

    /**
    Выбранные добавки для пиццы
     */
    val adds: List<MealAdditional> = emptyList<MealAdditional>(),

    /**
    Выбранные модификаторы (по группаам)
     */
    val modifiers: List<ModifierGroup> = emptyList<ModifierGroup>()
)