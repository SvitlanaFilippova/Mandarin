package com.mandarinkafe.mandarin.core.data.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class Cause(
    val id: String,
    val name: String
)