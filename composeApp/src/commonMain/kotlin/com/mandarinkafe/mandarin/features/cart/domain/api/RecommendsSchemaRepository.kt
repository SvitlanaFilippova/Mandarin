package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.features.cart.domain.models.RecommendsSchemaRule
import com.mandarinkafe.mandarin.util.Resource

interface RecommendsSchemaRepository {
    suspend fun getRecommendsSchema(): Resource<List<RecommendsSchemaRule>>
}