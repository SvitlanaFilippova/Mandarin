package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TerminalGroupDto(
    val items: List<TerminalItemDto>,
    val organizationId: String,
)





