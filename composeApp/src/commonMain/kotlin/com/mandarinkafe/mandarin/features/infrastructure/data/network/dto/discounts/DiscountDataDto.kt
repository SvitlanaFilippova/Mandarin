package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts

import kotlinx.serialization.Serializable

@Serializable
data class DiscountDataDto(
    val items: List<DiscountTypeDto>,
    val organizationId: String
)





