package com.mandarinkafe.mandarin.core.domain.models.extensions

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal

fun Map<CustomizedMeal, Int>.getTotalQuantityByMealId(mealId: String): Int {
    return this.filter { it.key.meal.id == mealId }
        .values
        .sum()
}

fun Map<CustomizedMeal, Int>.getTotalPriceByMealId(mealId: String): Int {
    return this.filter { it.key.meal.id == mealId }.entries
        .sumOf { (item, quantity) ->
            item.totalPrice() * quantity
        }
}

fun CustomizedMeal.totalPrice(): Int {
    val addsTotal = adds.sumOf { it.price }
    val modifiersTotal = modifiers.sumOf { group -> group.items.sumOf { it.price } }
    return meal.price + addsTotal + modifiersTotal
}

/**
 * Проверяет, выбраны ли какие-либо опции у блюда
 */
fun CustomizedMeal.isCustomized(): Boolean {
    return modifiers.isNotEmpty() || adds.isNotEmpty()
}

/**
 * Генерирует текстовое описание выбранных опций блюда.
 *
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
    } else null

    val optionalItems = buildList {
        optionalGroups.forEach { group ->
            addAll(group.items.map { "+ ${it.name}" })
        }
        adds.forEach { add ->
            add("+ ${add.name}")
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

fun CustomizedMeal.hasSelectedAllRequiredModifiers(): Boolean {
    return meal.modifiers
        .filter { it.isRequired }
        .all { group ->
            val selectedGroup = modifiers.find { it.id == group.id }
            selectedGroup != null && selectedGroup.items.isNotEmpty()
        }

}

fun CustomizedMeal.isFavorite(favorites: List<CustomizedMeal>): Boolean {
    return favorites.any { it == this }
}
