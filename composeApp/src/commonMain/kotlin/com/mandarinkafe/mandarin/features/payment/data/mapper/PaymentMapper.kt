package com.mandarinkafe.mandarin.features.payment.data.mapper

import com.mandarinkafe.mandarin.features.payment.data.dto.PaymentStatusResponse
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus

fun PaymentStatusResponse.toDomain(): PaymentInfo {
    return PaymentInfo(
        paymentId = payment_id,
        orderId = order_id ?: "",
        status = PaymentStatus.fromString(status),
        paid = paid ?: false,
        amountValue = amount_value,
        amountCurrency = amount_currency,
        description = description,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

