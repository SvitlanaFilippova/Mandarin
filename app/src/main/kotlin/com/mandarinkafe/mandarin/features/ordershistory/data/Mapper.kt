package com.mandarinkafe.mandarin.features.ordershistory.data

import com.mandarinkafe.mandarin.db.Saved_order
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

object Mapper {
    fun Saved_order.toSavedOrder() = SavedOrder(
        id = id,
        timestamp = timestamp,
        number = number,
        whenCreated = whenCreated,
        orderType = orderType.toDeliveryTypeOrNull(),
        addressLine1 = addressLine1,
        addressDetails = addressDetails,
        mealNames = mealNames,
    )

    private fun String?.toDeliveryTypeOrNull(): DeliveryType? {
        if (this.isNullOrBlank()) return null
        return when (trim().lowercase()) {
            "delivery", "доставка", "доставка курьером" -> DeliveryType.DELIVERY
            "self_pickup", "selfpickup", "самовывоз" -> DeliveryType.SELF_PICKUP
            else -> null
        }
    }
}