package com.mandarinkafe.mandarin.menu.domain.models

data class ModifierGroup(
    val id: String,
    val name: String,
    val sku: String,
    val items: List<ModifierItem>
)