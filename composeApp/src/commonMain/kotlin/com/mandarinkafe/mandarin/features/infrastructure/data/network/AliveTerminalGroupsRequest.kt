package com.mandarinkafe.mandarin.features.infrastructure.data.network

import kotlinx.serialization.Serializable

@Serializable
data class AliveTerminalGroupsRequest(
    val organizationIds: List<String>,
    val terminalGroupIds: List<String>,
)





