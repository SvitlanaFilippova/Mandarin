package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
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

    ) {
    override fun equals(other: Any?): Boolean {
        return other is CustomizedMeal && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

val CustomizedMeal.id: String
    get() = generateUniqueId(meal, adds, modifiers)

/**
 * Проверяет, выбраны ли какие-либо опции у блюда
 */
val CustomizedMeal.isCustomized
    get() = modifiers.isNotEmpty() || adds.isNotEmpty()

fun CustomizedMeal.isFavorite(favorites: List<CustomizedMeal>): Boolean {
    return favorites.any { it == this }
}

fun CustomizedMeal.totalPrice(): Int {
    val addsTotal = adds.sumOf { it.price }
    val modifiersTotal = modifiers.sumOf { group -> group.items.sumOf { it.price } }
    return meal.price + addsTotal + modifiersTotal
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

private fun generateUniqueId(
    meal: Meal,
    adds: List<MealAdditional>,
    modifiers: List<ModifierGroup>
): String {
    return buildString {
        append(meal.id)
        if (adds.isNotEmpty() || modifiers.isNotEmpty()) append("_custom")
        adds.sortedBy { it.id }.forEach { append("_add_${it.id}") }
        modifiers.sortedBy { it.id }.forEach { g ->
            append("_modgroup_${g.id}")
            g.items.sortedBy { it.id }.forEach { append("_mod_${it.id}") }
        }
    }
}