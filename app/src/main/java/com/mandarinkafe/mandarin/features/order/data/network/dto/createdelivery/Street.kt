package com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery

data class Street(
    val name: String,
    val id: String? = null,
    val city: CityDto? = null,
)

data class CityDto(
    val name: String,
    val id: String
)
