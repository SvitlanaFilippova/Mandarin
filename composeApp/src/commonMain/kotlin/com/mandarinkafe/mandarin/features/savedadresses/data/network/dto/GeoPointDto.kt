package com.mandarinkafe.mandarin.features.savedadresses.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeoPointDto(
    val latitude: Double,
    val longitude: Double,
)

