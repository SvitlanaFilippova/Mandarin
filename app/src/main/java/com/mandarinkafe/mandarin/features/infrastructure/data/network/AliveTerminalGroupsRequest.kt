package com.mandarinkafe.mandarin.features.infrastructure.data.network

data class AliveTerminalGroupsRequest(
    val organizationIds: List<String>,
    val terminalGroupIds: List<String>
)