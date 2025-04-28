package com.mandarinkafe.mandarin.menu.domain.models

import com.mandarinkafe.mandarin.core.domain.models.MealAdditional

data class MealAdditionalCategory(
    val id: String,
    val name: String,
    val mealAdditionals: List<MealAdditional>?,
    val isHidden: Boolean
)