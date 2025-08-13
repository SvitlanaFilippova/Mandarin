package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

data class TerminalItemDto(
    val address: String,
    val id: String,
    val name: String,
    val organizationId: String,
    val timeZone: String
)