package com.mandarinkafe.mandarin.core.domain.models


data class DeliveryArea(
    val id: Int,
    val polygon: List<GeoPoint>,
    val parentArea: List<GeoPoint>?, // Для вычитания меньших зон
    val deliveryPrice: Int,
    val freeDeliveryThreshold: Int
)