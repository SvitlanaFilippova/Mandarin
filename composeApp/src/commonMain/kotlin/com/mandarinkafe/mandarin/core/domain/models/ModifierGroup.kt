package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class ModifierGroup(
    val id: String,
    val name: String,
    val items: List<ModifierItem>,
    val isSingleChoice: Boolean,
    val isRequired: Boolean,
    val maxQuantity: Int,
)