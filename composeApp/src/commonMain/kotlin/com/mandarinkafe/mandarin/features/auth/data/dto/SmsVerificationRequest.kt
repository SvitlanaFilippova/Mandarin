package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SmsVerificationRequest(
    val phone: String,
    val platform: String,
    /**
     * Хэш подписи приложения для SMS Retriever API (только для Android)
     * 11-символьный Base64 хэш подписи приложения
     * null для iOS или если не удалось получить хэш
     */
    val appSignatureHash: String? = null,
)










