package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.totalPrice

data class CartSummary(
    val items: List<CartItem> = emptyList(),
    val containNotDiscountable: Boolean = false,
    val discountCategory: Int = 0,
    val cartSumWithDiscount: Double = 0.0,
) {
    val totalCartSum: Int
        get() = items.sumOf { (item, quantity) ->
            item.totalPrice() * quantity
        }

    val discountSum: Double
        get() = totalCartSum - cartSumWithDiscount
}