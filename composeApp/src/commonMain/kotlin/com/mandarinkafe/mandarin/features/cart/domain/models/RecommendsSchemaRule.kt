package com.mandarinkafe.mandarin.features.cart.domain.models

data class RecommendsSchemaRule(
    val sourceName: String,
    val excludeSku: List<String>,
    val recommendedSku: List<String>,
    val isSeparate: Boolean = false
)