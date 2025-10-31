package com.mandarinkafe.mandarin.features.infrastructure.data.network

import kotlinx.serialization.Serializable

@Serializable
data class TerminalGroupsIdsRequest(
    val organizationIds: List<String>,
)





