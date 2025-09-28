package com.mandarinkafe.mandarin.features.menu.data.dto

data class ModifierItemDto(
    val itemId: String,
    val name: String? = null,
    val description: String? = null,
    val buttonImageUrl: String? = null,
    val prices: List<PriceDto>?,
    val portionWeightGrams: Double? = null,
    val productCategoryId: Any? = null,
    val restrictions: RestrictionsDto? = null,
    val amount: Double? = null,
)