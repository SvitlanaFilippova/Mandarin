package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SmsVerificationRequest(
    val phone: String,
    val platform: String,
)










