package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toDomain
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class RecommendsSchemaRepositoryImpl(private val networkClient: GoogleDocsNetworkClient) :
    RecommendsSchemaRepository {
    override suspend fun getRecommendsSchema(): Resource<List<RecommendsSchemaRule>> {
        val response = networkClient.getRecommendations()

        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }

        if (response.resultCode != HTTP_SUCCESS) {
            return Resource.ErrorEmptyData()
        }

        val csvText = (response as CsvResponse).csv
            ?: return Resource.ErrorOther("Нет валидной схемы")

        val recommendsSchemaDto = parseCsv(csvText)
        if (recommendsSchemaDto.isEmpty()) {
            return Resource.ErrorOther("Нет валидной схемы")
        }

        val result = recommendsSchemaDto.map { it.toDomain() }
        return Resource.Success(result)
    }

    private fun parseCsv(csv: String): List<RecommendsSchemaDto> {
        return csv
            .lineSequence()
            .drop(1) // пропускаем заголовок
            .mapNotNull { line ->
                val parts = line.split(",")

                if (parts.size < CSV_COLUMNS_NUMBER) return@mapNotNull null

                val sourceName = parts[0].takeIf { it.isNotBlank() }

                val excludeSku = parts[1]
                    .takeIf { it.isNotBlank() }
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }

                val recommendedSku = parts[2]
                    .takeIf { it.isNotBlank() }
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }

                val isSeparate = parts[3].trim().equals(TRUE_STRING, ignoreCase = true)

                // Не создаём DTO, если нет исходного или нет рекомендуемых товаров
                if (sourceName == null) return@mapNotNull null
                if (recommendedSku.isNullOrEmpty()) return@mapNotNull null

                RecommendsSchemaDto(
                    sourceName = sourceName,
                    excludeSku = excludeSku,
                    recommendedSku = recommendedSku,
                    isSeparate = isSeparate
                )
            }
            .toList()
    }

    companion object {
        private const val CSV_COLUMNS_NUMBER = 4
        private const val TRUE_STRING = "TRUE"
    }
}