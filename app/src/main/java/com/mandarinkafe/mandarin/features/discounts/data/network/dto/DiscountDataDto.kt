package com.mandarinkafe.mandarin.features.discounts.data.network.dto

data class DiscountDataDto(
    val discountTypes: List<DiscountTypeDto>,
    val organizationId: String
)