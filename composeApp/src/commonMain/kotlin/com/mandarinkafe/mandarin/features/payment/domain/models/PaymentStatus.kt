package com.mandarinkafe.mandarin.features.payment.domain.models

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

