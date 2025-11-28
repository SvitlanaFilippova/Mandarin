package com.mandarinkafe.mandarin.features.order.domain.models

data class PaymentType(
    val id: String,
    val code: String,
    val paymentTypeKind: String,
)

