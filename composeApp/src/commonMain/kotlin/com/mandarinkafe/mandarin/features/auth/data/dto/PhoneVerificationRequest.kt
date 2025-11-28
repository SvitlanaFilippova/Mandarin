package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationRequest(
    val phone: String,
)

