package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO для ответа от /auth/request_sms
 * Содержит информацию о запрошенной SMS верификации
 */
@Serializable
data class SmsVerificationDataDto(
    @SerialName("status")
    val status: String? = null,
    @SerialName("expires_in")
    val expiresIn: Int? = null,
)







