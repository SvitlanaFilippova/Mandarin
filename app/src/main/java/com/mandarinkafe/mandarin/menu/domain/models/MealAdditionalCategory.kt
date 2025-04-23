package com.mandarinkafe.mandarin.menu.domain.models

data class MealAdditionalCategory(
    val id: String,
    val name: String,
    val mealAdditionals: List<MealAdditional>?,
    val isHidden: Boolean
)