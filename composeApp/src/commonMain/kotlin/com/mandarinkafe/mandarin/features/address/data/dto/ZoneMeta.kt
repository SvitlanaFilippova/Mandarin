package com.mandarinkafe.mandarin.features.address.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ZoneMeta(
    val id: Int,
    val freeDeliveryThreshold: Int,
    val colorHex: String
)
