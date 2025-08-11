package com.mandarinkafe.mandarin.features.discounts.data.network.dto

data class DiscountDataDto(
    val items: List<DiscountTypeDto>,
    val organizationId: String
)