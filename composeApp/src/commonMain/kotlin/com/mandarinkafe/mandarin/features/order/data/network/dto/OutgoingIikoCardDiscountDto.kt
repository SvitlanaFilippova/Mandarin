package com.mandarinkafe.mandarin.features.order.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutgoingIikoCardDiscountDto(
    val programId: String, // Card program ID
    val programName: String, // Card program name
    val discountItems: List<OutgoingDiscountItemDto>, // Discount information for order items
    val type: String, // iikoCard
)

