package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MenuDataDTO(
    val intervals: List<IntervalDto>?,
    val itemCategories: List<CategoryDto>?,
    val revision: Int
)





