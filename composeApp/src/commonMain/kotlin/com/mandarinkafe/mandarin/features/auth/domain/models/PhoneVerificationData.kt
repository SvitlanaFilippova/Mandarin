package com.mandarinkafe.mandarin.features.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationData(
    val checkId: String,
    val phoneToCall: String,
    val expiresInSeconds: Int?,
)