package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class Payment(
    val paymentTypeKind: String, // Cash/Card/External
    val sum: Double,
    val paymentTypeId: String, // Can be obtained by /api/1/payment_types
    val isPrepay: Boolean,
)