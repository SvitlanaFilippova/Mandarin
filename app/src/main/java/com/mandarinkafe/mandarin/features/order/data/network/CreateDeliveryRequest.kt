package com.mandarinkafe.mandarin.features.order.data.network

import com.mandarinkafe.mandarin.core.data.dto.order.OrderDto

data class CreateDeliveryRequest(
    val order: OrderDto,
    val organizationId: String,
)