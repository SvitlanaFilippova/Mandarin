package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendationsResponse
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.models.RecommendsSchemaRule
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class RecommendsSchemaRepositoryImpl(
    private val networkClient: ServerNetworkClient,
) : RecommendsSchemaRepository {

    override suspend fun getRecommendsSchema(): Resource<List<RecommendsSchemaRule>> {
        val response = networkClient.getRecommendations()

        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet()
            HTTP_SUCCESS -> {
                val data = (response as RecommendationsResponse).data.map { it.toDomain() }
                Resource.Success(data)
            }

            else -> Resource.ErrorEmptyData()
        }
    }
}
