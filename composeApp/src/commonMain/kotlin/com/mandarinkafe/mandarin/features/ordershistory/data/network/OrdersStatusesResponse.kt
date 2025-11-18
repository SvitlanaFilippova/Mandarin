package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class OrdersStatusesResponse(
    val orders: List<OrderStatusDto>? = null,
) : Response()

@Serializable
data class OrderStatusDto(
    val id: String,
    val status: String? = null,
)

