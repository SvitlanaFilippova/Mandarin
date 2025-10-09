package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MealDto(
    val itemId: String,
    val name: String,
    val description: String?,
    val sku: String?,
    val tags: List<TagDto>?,
    val labels: List<LabelDto>?,
    val itemSizes: List<ItemSizeDto>?,
    val orderItemType: String,
    val isHidden: Boolean?
)