package com.mandarinkafe.mandarin.menu.data.dto

data class MealDto(
    val itemId: String,
    val name: String,
    val description: String?,
    val tags: List<TagDto>?,
    val labels: List<LabelDto>?,
    val itemSizes: List<ItemSize>?,
    val isHidden: Boolean?
)