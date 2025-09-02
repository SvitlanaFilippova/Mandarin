package com.mandarinkafe.mandarin.features.order.data.network.dto

data class OutgoingDiscountTypeDto(
    val discountTypeId: String, // ID скидки
    val selectivePositions: List<String>, // список positionId товаров, на которые распространяется скидка
    val type: String, // RMS или iikoCard
)