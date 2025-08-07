package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable
import java.util.UUID

@Stable
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val customizedMeal: CustomizedMeal,
    val quantity: Int = 1,
    val comment: String = ""
)