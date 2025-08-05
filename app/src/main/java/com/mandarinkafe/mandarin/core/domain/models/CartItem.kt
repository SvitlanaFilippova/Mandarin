package com.mandarinkafe.mandarin.core.domain.models

data class CartItem(
    val customizedMeal: CustomizedMeal,
    val quantity: Int = 1,
    val comment: String = ""
)