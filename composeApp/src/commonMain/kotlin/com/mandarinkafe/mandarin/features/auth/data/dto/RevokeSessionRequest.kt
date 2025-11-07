package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RevokeSessionRequest(
    @SerialName("token_id")
    val tokenId: String,
)

