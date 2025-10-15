package com.mandarinkafe.mandarin.features.order.presentation.models

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType

enum class UiDeliveryType(
    val nameRes: String,
    val iconRes: String
) {
    DELIVERY(
        nameRes = "Доставка",
        iconRes = "ic_courier"
    ),
    PICKUP(
        nameRes = "Самовывоз",
        iconRes = "ic_pickup"
    )
}

fun DeliveryType.toUi(): UiDeliveryType {
    return UiDeliveryType.valueOf(this.name)
}

fun UiDeliveryType.toDomain(): DeliveryType {
    return DeliveryType.valueOf(this.name)
}