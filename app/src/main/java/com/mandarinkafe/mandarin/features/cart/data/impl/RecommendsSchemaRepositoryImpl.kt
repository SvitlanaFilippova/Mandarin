package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toDomain
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendationsResponse
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class RecommendsSchemaRepositoryImpl(private val networkClient: ServerNetworkClient) :
    RecommendsSchemaRepository {
    override suspend fun getRecommendsSchema(): Resource<List<RecommendsSchemaRule>> {
        val response = networkClient.getRecommendations()

        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }

        if (response.resultCode != HTTP_SUCCESS) {
            return Resource.ErrorEmptyData()
        }


        val result = (response as RecommendationsResponse).data.map { it.toDomain() }
        return Resource.Success(result)
    }
}