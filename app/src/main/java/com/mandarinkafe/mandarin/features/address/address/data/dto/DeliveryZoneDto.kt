package com.mandarinkafe.mandarin.features.address.address.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryZoneDto(
    @SerialName("free_delivery_threshold")
    val freeDeliveryThreshold: Int,

    @SerialName("color_hex")
    val colorHex: String?,

    val id: Int,
    val points: List<DeliveryPointDto>
)

@Serializable
data class DeliveryPointDto(
    val id: Int,
    val geometry: GeometryDto
)

@Serializable
data class GeometryDto(
    val type: String,
    val coordinates: List<List<List<Double>>>
)