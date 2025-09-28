package com.mandarinkafe.mandarin.core.data.dto.order

data class AddressDto(
    val street: StreetDto? = null,
    val house: String? = null,
    val flat: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val doorphone: String? = null,
    val type: String,
    val line1: String? = null,
)
