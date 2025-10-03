package com.mandarinkafe.mandarin.features.orderinfo.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderType
import kotlinx.serialization.Serializable

@Serializable
data class IncomingOrderDto(
    val phone: String? = null,
    val orderServiceType: String? = null,
    val deliveryPoint: DeliveryPointDto? = null,
    val comment: String? = null,
    val customer: CustomerDto? = null,
    val items: List<IncomingOrderItemDto> = emptyList(),
    val payments: List<PaymentDto>? = null,
    val status: String? = null,
    val deliveryDuration: Int? = null,
    val cancelInfo: CancelInfo? = null,
    val discounts: List<IncomingDiscountInfoDto>? = null,
    val orderType: OrderType? = null,
    val processedPaymentsSum: Double? = null,
    val sum: Double? = null,
    val whenClosed: String? = null,
    val whenConfirmed: String? = null,
    val whenCookingCompleted: String? = null,
    val whenCreated: String? = null,
    val whenDelivered: String? = null,
    val whenSended: String? = null,
    val problem: Problem? = null,
    val number: String? = null
)