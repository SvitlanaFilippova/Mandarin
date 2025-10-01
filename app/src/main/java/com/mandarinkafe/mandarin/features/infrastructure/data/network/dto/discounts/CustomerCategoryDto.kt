package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts

import kotlinx.serialization.Serializable

@Serializable
data class CustomerCategoryDto(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isDefaultForNewGuests: Boolean
)