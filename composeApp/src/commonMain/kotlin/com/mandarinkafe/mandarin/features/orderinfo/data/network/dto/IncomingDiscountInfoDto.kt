package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingDiscountInfoDto(
    val discountType: IncomingDiscountTypeDto? = null,
    val sum: Double? = null,
    val selectivePositions: List<String>? = null, // позиции, на которые не действует
)
