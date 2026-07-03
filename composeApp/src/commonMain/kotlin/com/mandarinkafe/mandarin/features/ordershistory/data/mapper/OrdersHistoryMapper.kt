package com.mandarinkafe.mandarin.features.ordershistory.data.mapper

import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.OrderInfoResponseDto
import com.mandarinkafe.mandarin.features.orderinfo.data.toDomain
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrderDetailsResponse
import com.mandarinkafe.mandarin.features.ordershistory.data.network.dto.SavedOrderDto
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
            status = status?.apiName,
            creationStatus = creationStatus?.apiName,
            errorInfo = errorInfo?.toDto(),
        )
    }

    fun SavedOrderDto.toDomain(): SavedOrder {
        val domainStatus = status?.toDeliveryStatus()
        val domainCreationStatus = creationStatus?.let { CreationStatus.fromApiName(it) }
        if (status != null && domainStatus == null) {
            Napier.w(
                "ORDERS_HISTORY_STATUS [Mapper] orderId=$id: статус '$status' НЕ распознан! Доступные статусы: ${
                    DeliveryStatus.entries.joinToString(
                        ", "
                    ) { it.apiName }
                }"
            )
        }
        if (creationStatus != null && domainCreationStatus == null) {
            Napier.w(
                "ORDERS_HISTORY_STATUS [Mapper] orderId=$id: статус создания '$creationStatus' НЕ распознан! Доступные статусы: ${
                    CreationStatus.entries.joinToString(", ") { it.apiName }
                }"
            )
        }
        return SavedOrder(
            id = id,
            number = number,
            timestamp = timestamp,
            whenCreated = whenCreated,
            orderType = orderType.takeIf { it.isNotBlank() }?.let { parseDeliveryType(it) },
            addressLine1 = addressLine1,
            addressDetails = addressDetails,
            mealNames = mealNames,
            status = domainStatus,
            creationStatus = domainCreationStatus,
            errorInfo = errorInfo?.toDomain(),
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

    fun String.toDeliveryStatus(): DeliveryStatus? {
        return DeliveryStatus.entries.find { it.apiName.equals(this, ignoreCase = true) }
    }

    private fun ErrorInfo.toDto() = ErrorInfoDto(
        code = code,
        message = message,
        userMessage = userMessage,
        errorReason = errorReason,
    )

    fun OrderDetailsResponse.toOrderInfoResponseDto(): OrderInfoResponseDto {
        return OrderInfoResponseDto(
            id = id,
            timestamp = timestamp,
            creationStatus = creationStatus,
            errorInfo = errorInfo,
            order = order,
            paymentDeadline = paymentDeadline,
        )
    }
}
