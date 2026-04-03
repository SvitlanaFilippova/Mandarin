package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderAcceptStatusDto(
    val isAcceptingOrders: Boolean = true,
    val orderAcceptanceEndTime: String? = null,
    val closingTime: String? = null,
    val serverTime: String = "",
)
