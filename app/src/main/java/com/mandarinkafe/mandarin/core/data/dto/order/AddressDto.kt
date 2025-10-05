package com.mandarinkafe.mandarin.core.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val street: StreetDto? = null,
    val house: String? = null,
    val flat: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val doorphone: String? = null,
    val type: String? = null,
    val line1: String? = null,
)
