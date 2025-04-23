package com.mandarinkafe.mandarin.menu.domain.models

data class ModifierItem(
    val id: String,
    val name: String,
    val sku: String,
    val isHidden: Boolean,
    val weight: Int,
    val price: Int,
)
