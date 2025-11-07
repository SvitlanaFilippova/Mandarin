package com.mandarinkafe.mandarin.features.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNameRequest(
    @SerialName("name")
    val name: String,
)

