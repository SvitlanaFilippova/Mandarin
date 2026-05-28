package com.mandarinkafe.mandarin.features.infrastructure.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class PhoneDiscountResponse(
    val discountPercent: Int? = null,
) : Response()
