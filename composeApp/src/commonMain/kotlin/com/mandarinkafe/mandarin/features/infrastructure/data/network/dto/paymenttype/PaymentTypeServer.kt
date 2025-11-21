package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype

import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import kotlinx.serialization.Serializable

@Serializable
data class PaymentTypeServer(
    val id: String,
    val code: String,
    val paymentTypeKind: String,
)

fun PaymentTypeServer.toDomain() = PaymentType(
    id = id,
    code = code,
    paymentTypeKind = paymentTypeKind
)


