package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val customizedMeal: CustomizedMeal,
    val quantity: Int = 1,
    val comment: String = ""
) {
    val name: String
        get() = customizedMeal.meal.name

    /**
     * Проверка эквивалентности по содержимому (без учёта id и quantity).
     */
    fun equalsByContent(other: CartItem): Boolean {
        return customizedMeal == other.customizedMeal &&
                comment == other.comment
    }
}