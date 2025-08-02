package com.mandarinkafe.mandarin.features.menu.domain.models

import com.mandarinkafe.mandarin.core.domain.models.MealAdditional

data class MealAdditionalCategory(
    val id: String,
    val name: String,
    val items: List<MealAdditional>?,
)