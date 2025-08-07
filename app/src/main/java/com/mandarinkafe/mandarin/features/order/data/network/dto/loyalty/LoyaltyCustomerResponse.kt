package com.mandarinkafe.mandarin.features.order.data.network.dto.loyalty

import com.mandarinkafe.mandarin.core.data.dto.Response

data class LoyaltyCustomerResponse(
    val id: String,
    val isDeleted: Boolean? = null,
    val categories: List<LoyaltyCustomerCategoryDto>
) : Response() {
    val maxDiscount: Int
        get() = categories.maxOfOrNull { it.name.toIntOrNull() ?: 0 } ?: 0
}
