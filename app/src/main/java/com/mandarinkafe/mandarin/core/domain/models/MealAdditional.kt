package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class MealAdditional(
    val id: String,
    val name: String,
    val price: Int,
    val orderItemType: String,
)