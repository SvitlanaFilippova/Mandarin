package com.mandarinkafe.mandarin.features.address.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryZoneDto(
    val name: String,

    @SerialName("free_delivery_threshold")
    val freeDeliveryThreshold: Int,

    @SerialName("color_hex")
    val colorHex: String?,

    val points: List<DeliveryPointDto>,
)

@Serializable
data class DeliveryPointDto(
    val id: Int,
    val geometry: GeometryDto,
)

@Serializable
sealed class GeometryDto {
    abstract val type: String

    @Serializable
    @SerialName("Polygon")
    data class PolygonGeometry(
        override val type: String,
        val coordinates: List<List<List<Double>>>,
    ) : GeometryDto()

    @Serializable
    @SerialName("MultiPolygon")
    data class MultiPolygonGeometry(
        override val type: String,
        val coordinates: List<List<List<List<Double>>>>,
    ) : GeometryDto()
}


