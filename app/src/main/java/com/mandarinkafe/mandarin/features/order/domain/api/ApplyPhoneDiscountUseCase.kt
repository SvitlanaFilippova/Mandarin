package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.features.order.domain.models.DiscountByPhoneResult

interface ApplyPhoneDiscountUseCase {
    suspend operator fun invoke(
        rawPhone: String,
        currentDiscount: Int
    ): DiscountByPhoneResult
}