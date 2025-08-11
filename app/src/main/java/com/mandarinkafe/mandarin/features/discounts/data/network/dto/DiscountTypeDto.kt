package com.mandarinkafe.mandarin.features.discounts.data.network.dto

data class DiscountTypeDto(
    val id: String,
    val name: String,
    val percent: Double,
    val isDeleted: Boolean,
    val canApplyByCardNumber: Boolean,
    val canBeAppliedSelectively: Boolean,
    val comment: String,
    val isAutomatic: Boolean,
    val isCard: Boolean,
    val isCategorisedDiscount: Boolean,
    val isManual: Boolean,
    val mode: String,
    val sum: Double
)