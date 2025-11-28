package com.mandarinkafe.mandarin.features.more.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppStoreDto(
    val id: String,
    val label: String,
    val url: String,
    val priority: Int? = null,
)
