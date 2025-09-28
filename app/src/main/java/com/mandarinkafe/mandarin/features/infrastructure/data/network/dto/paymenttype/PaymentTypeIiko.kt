package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype

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
