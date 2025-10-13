package com.mandarinkafe.mandarin.features.ordershistory.data

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

object Mapper {
    
    fun String?.toDeliveryTypeOrNull(): DeliveryType? {
        if (this.isNullOrBlank()) return null
        return when (trim().lowercase()) {
            "delivery", "доставка", "доставка курьером" -> DeliveryType.DELIVERY
            "self_pickup", "selfpickup", "самовывоз" -> DeliveryType.SELF_PICKUP
            else -> null
        }
    }
}
