package com.mandarinkafe.mandarin.features.order.data.network

import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateDeliveryRequest(
    val order: OutgoingOrderDto,
    val organizationId: String,
)