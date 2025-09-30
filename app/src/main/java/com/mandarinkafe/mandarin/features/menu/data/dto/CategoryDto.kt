package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val items: List<MealDto> = emptyList(),
    val buttonImageUrl: String?,
    val description: String?,
    val isHidden: Boolean?,
    val tags: List<TagDto>?,
    val labels: List<LabelDto>?
)