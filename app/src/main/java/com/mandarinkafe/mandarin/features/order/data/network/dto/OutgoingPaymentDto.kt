package com.mandarinkafe.mandarin.features.order.data.network.dto

data class OutgoingPaymentDto(
    val paymentTypeKind: String,
    val sum: Double,
    val paymentTypeId: String,
)