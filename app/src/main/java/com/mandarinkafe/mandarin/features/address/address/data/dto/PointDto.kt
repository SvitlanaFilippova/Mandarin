package com.mandarinkafe.mandarin.features.address.address.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val lat: Double,
    val lng: Double
)