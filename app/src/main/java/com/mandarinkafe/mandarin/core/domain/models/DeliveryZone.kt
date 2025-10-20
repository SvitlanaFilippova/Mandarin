package com.mandarinkafe.mandarin.core.domain.models

    data class DeliveryZone(
        val id: Int,
        val polygon: List<GeoPoint>,
        val parentArea: List<GeoPoint>?, // Для вычитания меньших зон
        val colorHex: String,
        val freeDeliveryThreshold: Int,
        val deliveryPrice: Int
    )