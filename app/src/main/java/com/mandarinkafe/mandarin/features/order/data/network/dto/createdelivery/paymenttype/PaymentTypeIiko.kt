package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.paymenttype

import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType

data class PaymentTypeIiko(
    val id: String,
    val code: String,
    val paymentTypeKind: String,
    val isDeleted: Boolean
)

fun PaymentTypeIiko.toDomain() = PaymentType(
    id = id,
    code = code,
    paymentTypeKind = paymentTypeKind
)
