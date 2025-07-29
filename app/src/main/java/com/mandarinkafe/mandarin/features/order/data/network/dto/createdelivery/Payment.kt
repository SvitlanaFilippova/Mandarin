package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class Payment(
    val paymentTypeKind: String,
    val sum: Double,
    val paymentTypeId: String,
)