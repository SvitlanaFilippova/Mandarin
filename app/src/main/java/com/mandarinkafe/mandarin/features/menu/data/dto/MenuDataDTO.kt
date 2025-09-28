package com.mandarinkafe.mandarin.features.menu.data.dto

data class MenuDataDTO(
    val intervals: List<IntervalDto>?,
    val itemCategories: List<CategoryDto>?,
    val revision: Int
)