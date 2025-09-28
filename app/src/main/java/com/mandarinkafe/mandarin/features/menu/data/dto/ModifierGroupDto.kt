package com.mandarinkafe.mandarin.features.menu.data.dto

data class ModifierGroupDto(
    val itemGroupId: String,
    val name: String? = null,
    val items: List<ModifierItemDto>? = null,
    val restrictions: RestrictionsDto? = null,
)