package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemSizeDto(
    val portionWeightGrams: Float? = null,
    val prices: List<PriceDto>? = null,
    val buttonImageUrl: String? = null,
    val itemModifierGroups: List<ModifierGroupDto>? = null,
    val measureUnitType: String? = null,
)