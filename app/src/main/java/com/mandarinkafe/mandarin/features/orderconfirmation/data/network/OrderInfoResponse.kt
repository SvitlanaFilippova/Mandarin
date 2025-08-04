package com.mandarinkafe.mandarin.features.orderconfirmation.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto.OrderInfoResponseDto

data class OrderInfoResponse(
    val correlationId: String,
    val orders: List<OrderInfoResponseDto>
) : Response()