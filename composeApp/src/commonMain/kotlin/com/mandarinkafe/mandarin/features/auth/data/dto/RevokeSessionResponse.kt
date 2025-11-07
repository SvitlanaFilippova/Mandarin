package com.mandarinkafe.mandarin.features.auth.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RevokeSessionDataDto(
    @SerialName("is_success")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String? = null,
)

class RevokeSessionResponse(
    val data: RevokeSessionDataDto? = null,
) : Response()

