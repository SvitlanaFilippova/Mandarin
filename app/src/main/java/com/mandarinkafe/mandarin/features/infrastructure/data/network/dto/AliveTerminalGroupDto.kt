package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

data class AliveTerminalGroupDto(
    val isAlive: Boolean,
    val organizationId: String,
    val terminalGroupId: String
)