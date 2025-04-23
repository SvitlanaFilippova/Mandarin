package com.mandarinkafe.mandarin.menu.data.dto

data class ItemSize(
    val portionWeightGrams: Float,
    val prices: List<PriceDto>,
    val buttonImageUrl: String,
    val itemModifierGroups: List<ModifierGroupDto>?
)