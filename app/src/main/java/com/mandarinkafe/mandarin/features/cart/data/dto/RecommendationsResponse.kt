package com.mandarinkafe.mandarin.features.cart.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationsResponse(
    val data: List<RecommendsSchemaDto>
) : Response()