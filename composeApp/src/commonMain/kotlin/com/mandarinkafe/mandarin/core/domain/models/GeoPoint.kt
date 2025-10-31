package com.mandarinkafe.mandarin.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)
