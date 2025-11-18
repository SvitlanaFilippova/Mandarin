package com.mandarinkafe.mandarin.navigation

import kotlinx.serialization.Serializable

/**
 * Минимальные параметры для навигации к MealDetails
 * Передаем только идентификаторы вместо полного объекта
 */
@Serializable
data class MealDetailsNavParams(
    val mealId: String,
    val addsIds: List<String> = emptyList(),
    val modifierIds: Map<String, List<String>> = emptyMap(), // groupId -> list of itemIds
    val comment: String = "",
    val cartItemId: String? = null, // для edit mode
)

