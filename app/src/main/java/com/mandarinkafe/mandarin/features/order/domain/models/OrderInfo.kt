package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.core.data.dto.order.CourierInfo
import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.dto.order.ItemDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderType
import com.mandarinkafe.mandarin.core.data.dto.order.PaymentDto
import com.mandarinkafe.mandarin.core.data.dto.order.Problem

data class OrderInfo(
    val id: String,
    val number: String?,
    val timestamp: Long,
    val creationStatus: CreationStatus,
    val errorInfo: ErrorInfo?,
    val phone: String? = null,
    val deliveryAddress: String? = null,
    val comment: String? = null,
    val customer: CustomerDto? = null,
    val items: List<ItemDto?>,
    val payments: List<PaymentDto>? = null,
    val status: String? = null,
    val deliveryDuration: Int? = null,
    val cancelInfo: String? = null,
    val courierInfo: CourierInfo? = null,
    val orderType: OrderType? = null,
    val processedPaymentsSum: Int? = null,
    val sum: Int? = null,
    val whenClosed: String? = null,
    val whenConfirmed: String? = null,
    val whenCookingCompleted: String? = null,
    val whenCreated: String? = null,
    val whenDelivered: String? = null,
    val whenPacked: String? = null,
    val whenPrinted: String? = null,
    val whenSended: String? = null,
    val problem: Problem? = null,
)
