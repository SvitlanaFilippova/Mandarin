package com.mandarinkafe.mandarin.core.data.dto.order

data class DeliveryPointDto(
    val coordinates: Coordinates? = null,
    val address: AddressDto? = null,
    val comment: String? = null,
)