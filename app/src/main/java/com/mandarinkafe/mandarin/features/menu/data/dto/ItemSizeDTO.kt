package com.mandarinkafe.mandarin.features.menu.data.dto

data class ItemSizeDTO(
    val portionWeightGrams: Float,
    val prices: List<PriceDto>,
    val buttonImageUrl: String,
    val itemModifierGroups: List<ModifierGroupDto>?,
    val measureUnitType: String,
)