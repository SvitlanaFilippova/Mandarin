package com.mandarinkafe.mandarin.features.order.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.dto.order.OrderInfoDto

data class CreateDeliveryResponse(
    val correlationId: String,
    val orderInfo: OrderInfoDto
) : Response()