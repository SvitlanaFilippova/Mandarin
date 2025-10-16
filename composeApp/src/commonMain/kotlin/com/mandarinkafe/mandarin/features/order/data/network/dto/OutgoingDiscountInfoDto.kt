package com.mandarinkafe.mandarin.features.order.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingDiscountInfoDto(
    val discounts: List<OutgoingDiscountTypeDto>
)





