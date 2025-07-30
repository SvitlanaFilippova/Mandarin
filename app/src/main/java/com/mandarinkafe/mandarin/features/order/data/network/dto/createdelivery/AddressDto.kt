package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class AddressDto(
    val street: Street,
    val house: String = "-",
    val flat: String,
    val entrance: String,
    val floor: String,
    val doorphone: String,
    val type: String
)

