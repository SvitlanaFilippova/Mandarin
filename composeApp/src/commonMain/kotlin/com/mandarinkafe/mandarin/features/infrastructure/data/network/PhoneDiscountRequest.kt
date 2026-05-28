package com.mandarinkafe.mandarin.features.infrastructure.data.network

import kotlinx.serialization.Serializable

@Serializable
data class PhoneDiscountRequest(
    val phone: String,
)
