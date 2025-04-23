package com.mandarinkafe.mandarin.menu.data.dto

data class ModifierItemDto(
    val itemId: String?,
    val name: String?,
    val sku: String?,
    val isHidden: Boolean?,
    val portionWeightGrams: Double?,
    val prices: List<PriceDto>?,
)