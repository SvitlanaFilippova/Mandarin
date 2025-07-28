package com.mandarinkafe.mandarin.features.order.presentation.models

import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType

enum class UiDeliveryType(val nameRes: Int, val iconRes: Int) {
    DELIVERY(nameRes = R.string.shipping, iconRes = R.drawable.ic_courier),
    SELF_PICKUP(nameRes = R.string.shipping_self_pickup, iconRes = R.drawable.ic_selfpickup),
}

fun DeliveryType.toUi(): UiDeliveryType {
    return UiDeliveryType.valueOf(this.name)
}

fun UiDeliveryType.toDomain(): DeliveryType {
    return DeliveryType.valueOf(this.name)
}
