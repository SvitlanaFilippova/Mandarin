package com.mandarinkafe.mandarin.features.auth.domain.models

import kotlinx.serialization.Serializable

/**
 * Domain модель для данных SMS верификации
 */
@Serializable
data class SmsVerificationData(
    val status: String? = null,
    val expiresIn: Int? = null,
)










