package com.mandarinkafe.mandarin.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

data class MenuResponse(
    val itemCategories: List<CategoryDto>?,
) : Response()

data class CategoryDto(
    val id: String,
    val name: String,
    val items: List<MealDto>,
    val buttonImageUrl: String?,
    val description: String?,
    val isHidden: Boolean
)
