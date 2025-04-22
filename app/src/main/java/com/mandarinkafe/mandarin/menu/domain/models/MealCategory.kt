package com.mandarinkafe.mandarin.menu.domain.models

data class MealCategory(
    val id: String,
    val name: String,
    val meals: List<Meal>?,
    val subCategories: List<MealCategory>?,
    val tabIcon: String?,
    val description: String,
    val isHidden: Boolean
)