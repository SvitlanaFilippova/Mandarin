package com.mandarinkafe.mandarin.menu.data.dto

data class ModifierItemDto(
    val itemId: String,
    val name: String?,
    val description: String,
    val buttonImageUrl: String?,
    val prices: List<PriceDto>?,
    val portionWeightGrams: Double?,
    val productCategoryId: Any?,
    val restrictions: RestrictionsDto?,
)