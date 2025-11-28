package com.mandarinkafe.mandarin.core.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryPointDto(
    val coordinates: Coordinates? = null,
    val address: AddressDto? = null,
    val comment: String? = null,
)





