package com.mandarinkafe.mandarin.features.orderinfo.data.network

import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto
import kotlinx.serialization.Serializable

@Serializable
data class AddPaymentsRequest(
    val organizationId: String,
    val orderId: String,
    val payments: List<OutgoingPaymentDto>,
)

