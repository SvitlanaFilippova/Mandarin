package com.mandarinkafe.mandarin.features.order.data.network

import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto
import kotlinx.serialization.Serializable

@Serializable
data class ServerCreateOrderRequest(
    val order: OutgoingOrderDto,
    val paymentMethodCode: String? = null,
)

@Serializable
data class ServerCancelOrderRequest(
    val cancelCauseId: String? = null,
    val cancelComment: String? = null,
)

@Serializable
data class ServerAddPaymentRequest(
    val payment: OutgoingPaymentDto,
)
