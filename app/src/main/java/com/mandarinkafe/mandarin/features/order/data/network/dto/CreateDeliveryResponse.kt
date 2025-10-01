package com.mandarinkafe.mandarin.features.order.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.OrderInfoResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateDeliveryResponse(
    val correlationId: String,
    val orderInfo: OrderInfoResponseDto
) : Response()