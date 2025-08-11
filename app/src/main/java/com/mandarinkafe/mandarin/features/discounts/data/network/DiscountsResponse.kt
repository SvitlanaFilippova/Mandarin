package com.mandarinkafe.mandarin.features.discounts.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.discounts.data.network.dto.DiscountDataDto

data class DiscountsResponse(
    val discounts: List<DiscountDataDto>
): Response()