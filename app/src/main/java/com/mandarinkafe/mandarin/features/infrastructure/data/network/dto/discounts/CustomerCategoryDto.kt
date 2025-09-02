package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts

data class CustomerCategoryDto(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isDefaultForNewGuests: Boolean
)