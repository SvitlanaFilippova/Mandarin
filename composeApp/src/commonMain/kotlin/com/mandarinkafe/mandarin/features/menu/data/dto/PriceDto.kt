package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PriceDto(
    val organizationId: String,
    val price: Double?,
)





