package com.mandarinkafe.mandarin.core.data.dto.order

data class PaymentDto(
    val paymentTypeKind: String,
    val sum: Double,
    val paymentTypeId: String,
)