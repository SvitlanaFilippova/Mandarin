package com.mandarinkafe.mandarin.menu.data.dto

data class ModifierGroupDto(
    val itemGroupId: String,
    val name: String?,
    val items: List<ModifierItemDto>?,
    val restrictions: RestrictionsDto?,
)