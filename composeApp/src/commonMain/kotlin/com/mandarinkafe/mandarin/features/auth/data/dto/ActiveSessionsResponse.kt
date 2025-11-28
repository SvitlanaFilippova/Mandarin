package com.mandarinkafe.mandarin.features.auth.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveSessionsDataDto(
    @SerialName("sessions")
    val sessions: List<ActiveSessionDto>,
)

class ActiveSessionsResponse(
    val data: ActiveSessionsDataDto? = null,
) : Response()

