package com.mandarinkafe.mandarin.shared.cart.data.dto

data class RecommendsSchemaDto(
    val sourceName: String?,
    val excludeSku: List<String>?,
    val recommendedSku: List<String>?
)