package com.mandarinkafe.mandarin.core.domain.models

data class ModifierGroup(
    val id: String,
    val name: String,
    val items: List<ModifierItem>,
    val isSingleChoice: Boolean,
    val isRequired: Boolean,
)