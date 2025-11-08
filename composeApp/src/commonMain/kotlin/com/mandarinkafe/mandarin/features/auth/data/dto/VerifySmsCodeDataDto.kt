package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO для ответа от /auth/verify_sms
 * Содержит результат проверки SMS кода
 */
@Serializable
data class VerifySmsCodeDataDto(
    @SerialName("is_verified")
    val isVerified: Boolean,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
)

