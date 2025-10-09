package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Immutable
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val customizedMeal: CustomizedMeal,
    val quantity: Int = 1,
    val comment: String = ""
) {
    val name: String
        get() = customizedMeal.meal.name

}

/**
 * Проверка эквивалентности по содержимому (без учёта id и quantity).
 */
fun CartItem.equalsByContent(other: CartItem): Boolean {
    return customizedMeal == other.customizedMeal &&
            comment == other.comment
}