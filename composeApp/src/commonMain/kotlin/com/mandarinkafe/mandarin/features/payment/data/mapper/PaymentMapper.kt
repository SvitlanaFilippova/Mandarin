package com.mandarinkafe.mandarin.features.payment.data.mapper

import com.mandarinkafe.mandarin.features.payment.data.dto.PaymentStatusResponse
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus

fun PaymentStatusResponse.toDomain(): PaymentInfo {
    return PaymentInfo(
        paymentId = paymentId,
        orderId = orderId ?: "",
        status = PaymentStatus.fromString(status),
        paid = paid ?: false,
        amountValue = amountValue,
        amountCurrency = amountCurrency,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        paymentMethodType = paymentMethodType
    )
}

