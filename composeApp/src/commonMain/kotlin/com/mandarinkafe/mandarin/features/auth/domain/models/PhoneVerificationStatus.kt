package com.mandarinkafe.mandarin.features.auth.domain.models

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens

/**
 * Domain модель для результата проверки верификации номера по звонку
 */
data class PhoneVerificationStatus(
    val phone: String? = null,
    val isVerified: Boolean? = null,
    val checkId: String? = null,
    val status: String? = null,
    val verifiedAt: String? = null,
    val shouldStopPolling: Boolean? = null,
    val expiresInSeconds: Int? = null,
    val tokens: AuthTokens?,
)