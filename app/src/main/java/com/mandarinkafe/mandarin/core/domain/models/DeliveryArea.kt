package com.mandarinkafe.mandarin.core.domain.models

import com.yandex.mapkit.geometry.Point

data class DeliveryArea(
    val id: Int,
    val polygon: List<Point>,
    val parentArea: List<Point>?, // Для вычитания меньших зон
    val deliveryPrice: Int,
    val freeDeliveryThreshold: Int
)