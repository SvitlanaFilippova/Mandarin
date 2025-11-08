package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveSessionDto(
    @SerialName("token_id")
    val tokenId: String,
    @SerialName("device_name")
    val deviceName: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("is_current")
    val isCurrent: Boolean? = false,
)

