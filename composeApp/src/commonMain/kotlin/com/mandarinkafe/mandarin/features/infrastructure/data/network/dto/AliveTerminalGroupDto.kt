package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AliveTerminalGroupDto(
    val isAlive: Boolean,
    val organizationId: String,
    val terminalGroupId: String
)




