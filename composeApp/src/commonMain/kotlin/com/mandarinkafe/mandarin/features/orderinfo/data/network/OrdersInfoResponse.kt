package com.mandarinkafe.mandarin.features.orderinfo.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.OrderInfoResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class OrdersInfoResponse(
    val correlationId: String,
    val orders: List<OrderInfoResponseDto>
) : Response()
