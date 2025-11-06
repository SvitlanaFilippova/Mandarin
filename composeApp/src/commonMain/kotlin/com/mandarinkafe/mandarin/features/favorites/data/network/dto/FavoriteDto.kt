package com.mandarinkafe.mandarin.features.favorites.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    val mealId: String,
    val addsIds: List<String> = emptyList(),
    val modifierIds: Map<String, List<String>> = emptyMap(),
    val timestamp: Long,
)