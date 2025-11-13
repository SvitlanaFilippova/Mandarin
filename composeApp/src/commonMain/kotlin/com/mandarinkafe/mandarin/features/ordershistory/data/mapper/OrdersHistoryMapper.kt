package com.mandarinkafe.mandarin.features.ordershistory.data.mapper

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
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
}

