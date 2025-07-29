package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class CreateDeliveryRequest(
    val order: OrderDto,
    val organizationId: String,
)