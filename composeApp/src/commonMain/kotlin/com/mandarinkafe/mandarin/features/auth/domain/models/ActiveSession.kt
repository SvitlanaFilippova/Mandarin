package com.mandarinkafe.mandarin.features.auth.domain.models

/**
 * Domain модель для активной сессии пользователя
 */
data class ActiveSession(
    val tokenId: String,
    val deviceName: String?,
    val createdAt: String?,
    val isCurrent: Boolean = false,
)

