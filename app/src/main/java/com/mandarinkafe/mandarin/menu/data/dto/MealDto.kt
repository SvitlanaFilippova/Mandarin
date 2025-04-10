package com.mandarinkafe.mandarin.menu.data.dto

data class MealDto(
    val itemId: String,
    val sku: String,
    val name: String,
    val description: String?,
    val tags: List<Tag>,
    val itemSizes: List<ItemSize>,
    val isHidden: Boolean
)