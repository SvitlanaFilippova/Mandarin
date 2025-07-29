package com.mandarinkafe.mandarin.features.order.data.impl.mapper

import com.mandarinkafe.mandarin.features.order.data.network.dto.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer

fun LoyaltyCustomerResponse.toDomain(): LoyaltyCustomer {
    return LoyaltyCustomer(
        id = id,
        isDeleted = isDeleted == true,
        maxDiscountPercent = maxDiscount
    )
}