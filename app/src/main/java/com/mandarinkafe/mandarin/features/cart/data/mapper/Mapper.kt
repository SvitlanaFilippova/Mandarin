package com.mandarinkafe.mandarin.features.cart.data.mapper

import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule

fun RecommendsSchemaDto.toDomain() = RecommendsSchemaRule(
    sourceName = sourceName ?: "",
    excludeSku = excludeSku ?: emptyList<String>(),
    recommendedSku = recommendedSku ?: emptyList()
)