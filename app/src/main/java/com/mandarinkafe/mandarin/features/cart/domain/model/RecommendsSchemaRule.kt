package com.mandarinkafe.mandarin.features.cart.domain.model

data class RecommendsSchemaRule(
    val sourceName: String,
    val excludeSku: List<String>,
    val recommendedSku: List<String>
)