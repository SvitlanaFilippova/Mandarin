package com.mandarinkafe.mandarin.features.savedadresses.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: String,
    val point: GeoPointDto? = null,
    val streetAndBuilding: String? = null,
    val addressType: String? = null, // "APARTMENT", "PRIVATE_HOUSE", "OTHER"
    val apartmentNumber: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val intercom: String? = null,
    val comment: String? = null,
)

