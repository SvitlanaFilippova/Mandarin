package com.mandarinkafe.mandarin.features.favorites.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("meal_id")
    val mealId: String,
    @SerialName("adds_ids")
    val addsIds: List<String> = emptyList(),
    @SerialName("modifier_ids")
    val modifierIds: Map<String, List<String>> = emptyMap(),
    @SerialName("created_at")
    val createdAt: Long, // время создания записи
    @SerialName("updated_at")
    val updatedAt: Long, // время последнего изменения записи
)