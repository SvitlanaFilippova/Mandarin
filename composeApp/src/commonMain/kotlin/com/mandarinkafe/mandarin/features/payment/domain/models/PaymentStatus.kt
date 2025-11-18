package com.mandarinkafe.mandarin.features.payment.domain.models

import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.StringResource

enum class PaymentStatus {
    PENDING,
    SUCCEEDED,
    CANCELED,
    UNKNOWN;

    companion object {
        fun fromString(status: String?): PaymentStatus {
            return when (status?.lowercase()) {
                "pending" -> PENDING
                "succeeded" -> SUCCEEDED
                "canceled" -> CANCELED
                else -> UNKNOWN
            }
        }
    }
}

fun PaymentStatus.toDisplayString(): StringResource {
    return when (this) {
        PaymentStatus.PENDING -> MR.strings.payment_status_pending
        PaymentStatus.SUCCEEDED -> MR.strings.payment_status_succeeded
        PaymentStatus.CANCELED -> MR.strings.payment_status_canceled
        PaymentStatus.UNKNOWN -> MR.strings.payment_status_unknown
    }
}

