package com.mandarinkafe.mandarin.core.data.dto.order

import com.mandarinkafe.mandarin.features.order.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo

data class OrderInfoDto(
    val id: String,
    val timestamp: Long,
    val creationStatus: String?, // Enum: "Success" "InProgress" "Error"
    val errorInfo: ErrorInfoDto?, // Required only if "creationStatus"="Error".
    val order: OrderDto?, // Field is filled up if "creationStatus"="Success".

)

fun OrderInfoDto.toDomain(): OrderInfo {
    return OrderInfo(
        id = id,
        timestamp = timestamp,
        creationStatus = creationStatus?.let { CreationStatus.valueOf(it) }
            ?: CreationStatus.InProgress,
        errorInfo = errorInfo?.toDomain(),
        phone = order?.phone,
        deliveryAddress = order?.deliveryPoint?.address.toString(),
        comment = order?.comment,
        customer = order?.customer,
        items = order?.items.orEmpty(),
        payments = order?.payments,
        status = order?.status,
        deliveryDuration = order?.deliveryDuration,
        cancelInfo = order?.cancelInfo?.comment, // или map по-другому
        courierInfo = order?.courierInfo,
        orderType = order?.orderType,
        processedPaymentsSum = order?.processedPaymentsSum,
        sum = order?.sum,
        whenClosed = order?.whenClosed,
        whenConfirmed = order?.whenConfirmed,
        whenCookingCompleted = order?.whenCookingCompleted,
        whenCreated = order?.whenCreated,
        whenDelivered = order?.whenDelivered,
        whenPacked = order?.whenPacked,
        whenPrinted = order?.whenPrinted,
        whenSended = order?.whenSended,
        problem = order?.problem,
        number = order?.number
    )
}