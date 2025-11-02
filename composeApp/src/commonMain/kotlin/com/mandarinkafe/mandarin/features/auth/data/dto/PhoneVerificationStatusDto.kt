package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationStatusDto(
    val phone: String,
    @SerialName("is_verified")
    val isVerified: Boolean,
    @SerialName("check_id")
    val checkId: String? = null,
    val status: String? = null,
    @SerialName("verified_at")
    val verifiedAt: String? = null,
    @SerialName("should_stop_polling")
    val shouldStopPolling: Boolean,
    @SerialName("expires_in_seconds")
    val expiresInSeconds: Int? = null,
)

