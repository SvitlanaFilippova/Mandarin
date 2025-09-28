package com.mandarinkafe.mandarin.features.cart.data.dto

data class RecommendsSchemaDto(
    val sourceName: String?,
    val excludeSku: List<String>?,
    val recommendedSku: List<String>?,
    val isSeparate: Boolean = false
)