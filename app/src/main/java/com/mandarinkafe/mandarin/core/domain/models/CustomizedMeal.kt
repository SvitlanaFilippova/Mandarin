package com.mandarinkafe.mandarin.core.domain.models

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
) {
    val id: String = buildString {
        append(meal.id)
        adds.sortedBy { it.id }.forEach { append("_add_${it.id}") }
        modifiers.sortedBy { it.id }.forEach { group ->
            append("_modgroup_${group.id}")
            group.items.sortedBy { it.id }.forEach {
                append("_mod_${it.id}")
            }
        }
    }
}