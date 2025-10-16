package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts

import kotlinx.serialization.Serializable

@Serializable
data class CustomerCategoryDto(
    val id: String? = null,
    val name: String? = null,
    val isActive: Boolean? = null,
    val isDefaultForNewGuests: Boolean? = null
)





