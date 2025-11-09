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
    @SerialName("created_at")
    val createdAt: Long, // время создания позиции
    @SerialName("updated_at")
    val updatedAt: Long, // время последнего изменения позиции
)

