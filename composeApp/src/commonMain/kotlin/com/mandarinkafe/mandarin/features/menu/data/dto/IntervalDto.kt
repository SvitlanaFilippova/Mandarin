package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class IntervalDto(
    val organizationId: String,
    val fromTime: String,
    val toTime: String,
)





