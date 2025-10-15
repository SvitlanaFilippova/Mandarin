package com.mandarinkafe.mandarin.features.order.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingDiscountTypeDto(
    val discountTypeId: String, // ID скидки
    val selectivePositions: List<String>, // список positionId товаров, на которые распространяется скидка
    val type: String, // RMS или iikoCard
)




