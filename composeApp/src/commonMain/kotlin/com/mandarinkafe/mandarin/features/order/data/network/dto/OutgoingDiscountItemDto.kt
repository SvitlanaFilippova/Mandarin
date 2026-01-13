package com.mandarinkafe.mandarin.features.order.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingDiscountItemDto(
    val positionId: String, // Position ID of order item
    val sum: Double, // Discount/surcharge sum
    val amount: Double, // Amount
)

