package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

data class IncomingDiscountInfoDto(
    val discountType: IncomingDiscountTypeDto?,
    val sum: Double?,
    val selectivePositions: List<String>?, // позиции, на которые не действует
)