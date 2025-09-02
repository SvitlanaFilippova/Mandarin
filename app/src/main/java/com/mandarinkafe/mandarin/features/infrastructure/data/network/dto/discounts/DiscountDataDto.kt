package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts

data class DiscountDataDto(
    val items: List<DiscountTypeDto>,
    val organizationId: String
)