package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.totalPrice

data class CartSummary(
    val items: List<CartItem> = emptyList(),
    val containNotDiscountable: Boolean = false,
    val discountPercent: Int = 0,
    val discountId: String? = null,
    val cartSumWithDiscount: Double = 0.0,
) {
    val totalCartSum: Int
        get() = items
            .filter { !it.customizedMeal.meal.isHidden }
            .sumOf {
                it.customizedMeal.totalPrice() * it.quantity
            }

    val discountSum: Double
        get() = totalCartSum - cartSumWithDiscount
}

