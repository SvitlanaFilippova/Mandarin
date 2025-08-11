package com.mandarinkafe.mandarin.features.discounts.data.network.dto

data class CustomerCategoryDto(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val isDefaultForNewGuests: Boolean
)