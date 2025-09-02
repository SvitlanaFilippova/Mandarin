package com.mandarinkafe.mandarin.features.infrastructure.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts.DiscountDataDto

data class DiscountsResponse(
    val discounts: List<DiscountDataDto>
) : Response()