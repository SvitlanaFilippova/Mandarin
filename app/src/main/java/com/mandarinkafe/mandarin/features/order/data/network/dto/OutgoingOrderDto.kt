package com.mandarinkafe.mandarin.features.order.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderType
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem

data class OutgoingOrderDto(
    val phone: String? = null,
    val orderServiceType: String? = null,
    val deliveryPoint: DeliveryPointDto? = null,
    val comment: String? = null,
    val customer: CustomerDto? = null,
    val items: List<OutgoingOrderItem?>,
    val payments: List<OutgoingPaymentDto>? = null,
    val orderType: OrderType? = null,
    val processedPaymentsSum: Int? = null,
    val sum: Int? = null,
)