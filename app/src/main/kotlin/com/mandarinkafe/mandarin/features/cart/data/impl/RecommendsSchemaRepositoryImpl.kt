package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.features.cart.data.Mapper.toDomain
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

                val sourceName = parts[SOURCE_NAME_INDEX].takeIf { it.isNotBlank() }

                val excludeSku = parts[EXCLUDE_SKU_INDEX]
                    .takeIf { it.isNotBlank() }
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }

                val recommendedSku = parts[RECOMMEND_SKU_INDEX]
                    .takeIf { it.isNotBlank() }
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }

                val isSeparate =
                    parts[IS_SEPARATE_INDEX].trim().equals(TRUE_STRING, ignoreCase = true)

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
        private const val SOURCE_NAME_INDEX = 0
        private const val EXCLUDE_SKU_INDEX = 1
        private const val RECOMMEND_SKU_INDEX = 2
        private const val IS_SEPARATE_INDEX = 3
        private const val CSV_COLUMNS_NUMBER = 4
        private const val TRUE_STRING = "TRUE"
    }
}