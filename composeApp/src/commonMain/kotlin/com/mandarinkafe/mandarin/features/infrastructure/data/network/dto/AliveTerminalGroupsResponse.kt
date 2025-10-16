package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class AliveTerminalGroupsResponse(
    val correlationId: String,
    val isAliveStatus: List<AliveTerminalGroupDto>
) : Response()





