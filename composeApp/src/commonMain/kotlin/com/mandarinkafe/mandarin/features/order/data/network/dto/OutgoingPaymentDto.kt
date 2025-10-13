package com.mandarinkafe.mandarin.features.order.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingPaymentDto(
    val paymentTypeKind: String,
    val sum: Double,
    val paymentTypeId: String,
    val isPrepay: Boolean,
    val isProcessedExternally: Boolean,
    val isFiscalizedExternally: Boolean
)
