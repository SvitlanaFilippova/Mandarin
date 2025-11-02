package com.mandarinkafe.mandarin.features.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationStatus(
    val phone: String,
    val isVerified: Boolean,
    val checkId: String? = null,
    val status: String? = null,
    val verifiedAt: String? = null,
    val shouldStopPolling: Boolean,
    val expiresInSeconds: Int? = null,
)