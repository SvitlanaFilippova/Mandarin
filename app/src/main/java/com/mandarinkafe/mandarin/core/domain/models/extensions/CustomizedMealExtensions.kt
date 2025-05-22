package com.mandarinkafe.mandarin.core.domain.models.extensions

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.cart.data.models.StoredCartItem

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

fun List<ModifierGroup>.validateBy(mealModifiers: List<ModifierGroup>): List<ModifierGroup> {
    return this.mapNotNull { selectedGroup ->
        val referenceGroup = mealModifiers.find { it.id == selectedGroup.id }
        if (referenceGroup != null) {
            val updatedItems = selectedGroup.items.mapNotNull { item ->
                referenceGroup.items.find { it.id == item.id }
            }
            if (updatedItems.isNotEmpty()) {
                selectedGroup.copy(items = updatedItems)
            } else null
        } else null
    }
}

fun StoredCartItem.sameAs(other: StoredCartItem): Boolean {
    return mealId == other.mealId &&
            addsIds.orEmpty() == other.addsIds.orEmpty() &&
            modifiers.orEmpty() == other.modifiers.orEmpty()
}

fun CustomizedMeal.hasSelectedAllRequiredModifiers(): Boolean {
    return meal.modifiers
        .filter { it.isRequired }
        .all { group ->
            val selectedGroup = modifiers.find { it.id == group.id }
            selectedGroup != null && selectedGroup.items.isNotEmpty()
        }
}
