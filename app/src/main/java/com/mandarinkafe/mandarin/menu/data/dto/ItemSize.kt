package com.mandarinkafe.mandarin.menu.data.dto

data class ItemSize(
    val portionWeightGrams: Double,
    val prices: List<Price>,
    val buttonImageUrl: String,
)