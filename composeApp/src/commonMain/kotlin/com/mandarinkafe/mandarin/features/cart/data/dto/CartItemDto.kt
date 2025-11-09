package com.mandarinkafe.mandarin.features.cart.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: String,
    @SerialName("meal_id")
    val mealId: String,
    @SerialName("adds_ids")
    val addsIds: List<String> = emptyList(),
    @SerialName("modifier_ids")
    val modifierIds: Map<String, List<String>> = emptyMap(),
    val quantity: Int,
    val comment: String,
    val timestamp: Long, // Время создания записи
)

