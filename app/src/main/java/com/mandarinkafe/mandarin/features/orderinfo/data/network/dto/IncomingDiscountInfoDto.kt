package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingDiscountInfoDto(
    val discountType: IncomingDiscountTypeDto?,
    val sum: Double?,
    val selectivePositions: List<String>?, // позиции, на которые не действует
)