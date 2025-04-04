package com.mandarinkafe.mandarin.menu.domain.models

import com.mandarinkafe.mandarin.menu.data.dto.Tag

data class Meal(
    val id: String,
    val sku: String,
    val name: String,
    val description: String?,
    val weight: Int?,
    val price: Int,
    val imageUrl: String,
    var categoryId: String?,
    var isFavorite: Boolean,
    val tags: List<Tag>?,
    val topCategoryId: String?,
    val isHidden: Boolean,
    val isEditable: Boolean
)