package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem

interface CalculateCartTotalWithDiscountUseCase {
    operator fun invoke(
        items: List<CartItem>,
        discountAmount: Int,
    ): Double
}