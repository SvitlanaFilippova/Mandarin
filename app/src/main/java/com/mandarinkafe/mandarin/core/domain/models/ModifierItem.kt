package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class ModifierItem(
    val id: String,
    val name: String,
    val price: Int,
    val weight: Int,
    val measureUnitType: MeasureUnitType,
)