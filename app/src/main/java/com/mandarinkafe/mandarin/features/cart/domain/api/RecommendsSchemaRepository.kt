package com.mandarinkafe.mandarin.features.cart.domain.api

import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule
import com.mandarinkafe.mandarin.util.Resource

interface RecommendsSchemaRepository {
    suspend fun getRecommendsSchema(): Resource<List<RecommendsSchemaRule>>
}