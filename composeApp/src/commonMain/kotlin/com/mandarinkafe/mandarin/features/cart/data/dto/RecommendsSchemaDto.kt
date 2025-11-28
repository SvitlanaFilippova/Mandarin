package com.mandarinkafe.mandarin.features.cart.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendsSchemaDto(
    @SerialName("source_name")
    val sourceName: String?,

    @SerialName("excluded_ids")
    val excludeSku: String?,

    @SerialName("recommended_ids")
    val recommendedSku: String?,

    @SerialName("show_as_block")
    val isSeparate: Boolean = false,
)
