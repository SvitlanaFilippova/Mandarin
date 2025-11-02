package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationStatusRequest(
    val phone: String,
)

