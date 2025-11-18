package com.mandarinkafe.mandarin.features.ordershistory.data.mapper

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.OrderInfoResponseDto
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrderDetailsResponse
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrderStatusDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.dto.SavedOrderDto
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.OrderStatus
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import io.github.aakira.napier.Napier

object OrdersHistoryMapper {

    fun SavedOrder.toDto(): SavedOrderDto {
        return SavedOrderDto(
            id = id,
            number = number,
            timestamp = timestamp,
            whenCreated = whenCreated,
            orderType = orderType?.name ?: "",
            addressLine1 = addressLine1,
            addressDetails = addressDetails,
            mealNames = mealNames,
            paymentMethodCode = paymentMethodCode,
            mealIds = mealIds,
        )
    }

    fun SavedOrderDto.toDomain(): SavedOrder {
        return SavedOrder(
            id = id,
            number = number,
            timestamp = timestamp,
            whenCreated = whenCreated,
            orderType = orderType.takeIf { it.isNotBlank() }?.let { parseDeliveryType(it) },
            addressLine1 = addressLine1,
            addressDetails = addressDetails,
            mealNames = mealNames,
            status = null, // status не хранится в истории, проверяется отдельно
            paymentMethodCode = paymentMethodCode,
            mealIds = mealIds,
        )
    }

    private fun parseDeliveryType(type: String): DeliveryType? {
        return try {
            DeliveryType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            Napier.e("OrdersHistoryMapper, parseDeliveryType error: $e")
            null
        }
    }

    fun OrderStatusDto.toDomain(): OrderStatus {
        return OrderStatus(
            orderId = id,
            status = status?.toDeliveryStatus()
        )
    }

    private fun String.toDeliveryStatus(): DeliveryStatus? {
        return DeliveryStatus.entries.find { it.apiName.equals(this, ignoreCase = true) }
    }

    fun OrderDetailsResponse.toOrderInfoResponseDto(): OrderInfoResponseDto {
        return OrderInfoResponseDto(
            id = id,
            timestamp = timestamp,
            creationStatus = creationStatus,
            errorInfo = errorInfo,
            order = order,
        )
    }
}

