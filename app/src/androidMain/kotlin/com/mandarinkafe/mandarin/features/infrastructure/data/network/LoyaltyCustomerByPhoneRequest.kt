package com.mandarinkafe.mandarin.features.infrastructure.data.network

import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyCustomerByPhoneRequest(
    val phone: String,
    val type: String = "phone",
    val organizationId: String,
)