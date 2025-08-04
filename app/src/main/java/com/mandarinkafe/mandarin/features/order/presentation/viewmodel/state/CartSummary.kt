package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.totalPrice

data class CartSummary(
    val items: Map<CustomizedMeal, Int> = emptyMap(),
    val containNotDiscountable: Boolean = false,
    val discountCategory: Int = 0,
    val cartSumWithDiscount: Double = 0.0,
) {
    val totalCartSum: Int
        get() = items.entries.sumOf { (item, quantity) ->
            item.totalPrice() * quantity
        }

    val discountSum: Double
        get() = totalCartSum - cartSumWithDiscount
}