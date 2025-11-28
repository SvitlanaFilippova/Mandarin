package com.mandarinkafe.mandarin.core.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class StreetDto(
    val name: String? = null,
    val id: String? = null,
    val city: CityDto? = null,
)





