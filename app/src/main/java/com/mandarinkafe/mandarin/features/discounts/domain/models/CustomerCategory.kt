package com.mandarinkafe.mandarin.features.discounts.domain.models

data class CustomerCategory(
    val id: String,
    val name: String,
    val discountPercent: Int?,
    val isActive: Boolean,
)
