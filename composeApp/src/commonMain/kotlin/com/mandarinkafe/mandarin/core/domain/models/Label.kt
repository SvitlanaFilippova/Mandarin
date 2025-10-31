package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Label(
    val id: String,
    val name: String,
)
