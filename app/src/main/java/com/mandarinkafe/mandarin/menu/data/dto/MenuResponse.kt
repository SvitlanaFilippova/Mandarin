package com.mandarinkafe.mandarin.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

data class MenuResponse(
    val itemCategories: List<CategoryDto>?,
) : Response()
