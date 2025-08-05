package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable

@Stable
data class CustomizedMeal(
    val meal: Meal,

    /**
    Выбранные добавки
     */
    val adds: List<MealAdditional> = emptyList<MealAdditional>(),

    /**
    Выбранные модификаторы (по группам)
     */
    val modifiers: List<ModifierGroup> = emptyList<ModifierGroup>(),

    val comment: String = ""
)

/**
 * Проверяет, выбраны ли какие-либо опции у блюда
 */
val CustomizedMeal.isCustomized
    get() = modifiers.isNotEmpty() || adds.isNotEmpty()

val CustomizedMeal.id: String
    get() = buildString {
        append(meal.id)
        adds.sortedBy { it.id }.forEach { append("_add_${it.id}") }
        modifiers.sortedBy { it.id }.forEach { group ->
            append("_modgroup_${group.id}")
            group.items.sortedBy { it.id }.forEach {
                append("_mod_${it.id}")
            }
        }
    }

fun CustomizedMeal.isFavorite(favorites: List<CustomizedMeal>) = favorites.any { it == this }

fun CustomizedMeal.totalPrice(): Int {
    val addsTotal = adds.sumOf { it.price }
    val modifiersTotal = modifiers.sumOf { group -> group.items.sumOf { it.price } }
    return meal.price + addsTotal + modifiersTotal
}

fun Map<CustomizedMeal, Int>.getTotalQuantityByMealId(mealId: String) =
    this.filter { it.key.meal.id == mealId }
        .values
        .sum()

fun Map<CustomizedMeal, Int>.getTotalPriceByMealId(mealId: String) =
    this.filter { it.key.meal.id == mealId }.entries
        .sumOf { (item, quantity) ->
            item.totalPrice() * quantity
        }

fun CustomizedMeal.hasSelectedAllRequiredModifiers(): Boolean {
    return meal.modifiers
        .filter { it.isRequired }
        .all { group ->
            val selectedGroup = modifiers.find { it.id == group.id }
            selectedGroup != null && selectedGroup.items.isNotEmpty()
        }

}

/**
 * Генерирует текстовое описание выбранных опций блюда.
 * - Если у блюда обязательный выбор (`requireSelection`), показываем модификаторы с их выбранными значениями.
 * - Если у блюда есть дополнительные модификаторы или добавки (isCustomizable), отображаем их с новой строки и префиксом "+".
 * - Если ничего не выбрано — возвращаем пустую строку.
 */
fun CustomizedMeal.customizedText(): String {
    val requiredGroups = modifiers.filter { it.isRequired }
    val optionalGroups = modifiers.filterNot { it.isRequired }

    val requiredText = if (meal.requireSelection && requiredGroups.isNotEmpty()) {
        requiredGroups.joinToString("; ") { group ->
            val itemsText = group.items.joinToString(", ") { it.name }
            "${group.name}: $itemsText"
        }
    } else {
        null
    }
    val optionalItems = buildList {
        optionalGroups.forEach { group ->
            addAll(group.items.map { "+\u00A0${it.name}" })
        }
        adds.forEach { add ->
            add("+\u00A0${add.name}")
        }
    }
    return buildString {
        if (!requiredText.isNullOrBlank()) append(requiredText)
        if (optionalItems.isNotEmpty()) {
            if (!requiredText.isNullOrBlank()) append("\n")
            append(optionalItems.joinToString(", "))
        }
    }
}