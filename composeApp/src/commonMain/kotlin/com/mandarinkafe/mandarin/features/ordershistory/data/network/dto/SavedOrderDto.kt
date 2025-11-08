package com.mandarinkafe.mandarin.features.ordershistory.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SavedOrderDto(
    val id: String,
    val number: String = "",
    val timestamp: Long,
    val whenCreated: String = "",
    val orderType: String? = null, // "DELIVERY" or "SELF_PICKUP"
    val addressLine1: String = "",
    val addressDetails: String = "",
    val mealNames: String = "",
)

