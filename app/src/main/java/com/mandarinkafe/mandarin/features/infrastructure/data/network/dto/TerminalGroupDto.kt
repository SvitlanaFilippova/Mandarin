package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

data class TerminalGroupDto(
    val items: List<TerminalItemDto>,
    val organizationId: String
)