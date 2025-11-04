package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationStatusDto(
    val phone: String? = null,
    @SerialName("is_verified")
    val isVerified: Boolean? = null,
    @SerialName("check_id")
    val checkId: String? = null,
    val status: String? = null,
    @SerialName("status_text")
    val statusText: String? = null,
    @SerialName("verified_at")
    val verifiedAt: String? = null,
    @SerialName("should_stop_polling")
    val shouldStopPolling: Boolean? = null,
    @SerialName("expires_in_seconds")
    val expiresInSeconds: Int? = null,
)

