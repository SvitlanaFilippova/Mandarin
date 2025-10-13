package com.mandarinkafe.mandarin.features.cart.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendsSchemaDto(
    val sourceName: String?,
    val excludeSku: List<String>?,
    val recommendedSku: List<String>?,
    val isSeparate: Boolean = false
)
