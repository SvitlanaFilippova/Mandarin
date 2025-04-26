package com.mandarinkafe.mandarin.core.domain.models

data class MealAdditional(
    val id: String,
    val name: String,
    val weight: Int,
    val price: Int,
    val isHidden: Boolean
)