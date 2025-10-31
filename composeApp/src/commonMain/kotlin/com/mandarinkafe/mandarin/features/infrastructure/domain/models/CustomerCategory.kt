package com.mandarinkafe.mandarin.features.infrastructure.domain.models

data class CustomerCategory(
    val id: String,
    val name: String,
    val discountPercent: Int?,
    val isActive: Boolean,
)