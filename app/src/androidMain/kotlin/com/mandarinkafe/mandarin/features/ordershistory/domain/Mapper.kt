package com.mandarinkafe.mandarin.features.ordershistory.domain

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.domain.models.getDetailsString
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.util.Constants.NON_BRAKING_SPACE
import com.mandarinkafe.mandarin.util.applyTypography

object Mapper {
    fun IncomingOrder.toSavedOrder(): SavedOrder {
        val names = items
            .groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .map { (name, totalAmount) -> "${name.applyTypography()}${NON_BRAKING_SPACE}x${totalAmount.toInt()}" }
            .joinToString(", ")

        return SavedOrder(
            id = id,
            number = number ?: "",
            timestamp = timestamp,
            whenCreated = whenCreated ?: "",
            orderType = orderType?.name.toDeliveryTypeOrNull(),
            addressLine1 = deliveryAddress?.streetAndBuilding ?: "",
            addressDetails = deliveryAddress?.getDetailsString() ?: "",
            mealNames = names
        )
    }

    private fun String?.toDeliveryTypeOrNull(): DeliveryType? {
        if (this.isNullOrBlank()) return null
        return when (trim().lowercase()) {
            "delivery", "доставка", "доставка курьером" -> DeliveryType.DELIVERY
            "self_pickup", "selfpickup", "самовывоз" -> DeliveryType.SELF_PICKUP
            else -> null
        }
    }
}