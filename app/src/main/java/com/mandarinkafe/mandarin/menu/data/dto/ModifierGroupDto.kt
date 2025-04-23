package com.mandarinkafe.mandarin.menu.data.dto

data class ModifierGroupDto(
    val itemGroupId: String?,
    val name: String?,
    val sku: String?,
    val items: List<ModifierItemDto>?
)