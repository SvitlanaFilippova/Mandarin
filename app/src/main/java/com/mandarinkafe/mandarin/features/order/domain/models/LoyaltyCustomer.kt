package com.mandarinkafe.mandarin.features.order.domain.models

data class LoyaltyCustomer(
    val id: String,
    val isDeleted: Boolean,
    val maxDiscountPercent: Int
)