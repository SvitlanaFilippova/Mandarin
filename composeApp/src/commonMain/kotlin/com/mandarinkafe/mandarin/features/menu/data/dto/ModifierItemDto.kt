package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ModifierItemDto(
    val itemId: String,
    val name: String? = null,
    val description: String? = null,
    val buttonImageUrl: String? = null,
    val prices: List<PriceDto>?,
    val portionWeightGrams: Double? = null,
    val measureUnitType: String? = null,
    val restrictions: RestrictionsDto? = null,
    val amount: Double? = null,
)
