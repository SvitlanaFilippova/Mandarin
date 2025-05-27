package com.mandarinkafe.mandarin.features.cart.data.impl

import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.cart.domain.CartMapper.toDomain
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule
import com.mandarinkafe.mandarin.util.Resource
import java.io.IOException

class RecommendsSchemaRepositoryImpl(private val networkClient: NetworkClient) :
    RecommendsSchemaRepository {
    override suspend fun getRecommendsSchema(): Resource<List<RecommendsSchemaRule>> {
        val response = networkClient.getRecommendations()

        return if (response.resultCode == -1) {
            Resource.Error("Проверьте подключение к интернету")
        } else {
            val csvText = (response as CsvResponse).csv ?: throw IOException("Пустой ответ")

            val recommendsSchemaDto = parseCsv(csvText)

            if (recommendsSchemaDto.isEmpty()) {
                Resource.Error("Нет валидной схемы")
            } else {
                val result = recommendsSchemaDto.map { it.toDomain() }
                Resource.Success(result)
            }
        }

    }

    private fun parseCsv(csv: String): List<RecommendsSchemaDto> {
        return csv
            .lineSequence()
            .drop(1) // пропустить заголовок
            .mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size < 3) return@mapNotNull null

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

                // Не создаём DTO, если вообще нет исходных данных или сопутствующих товаров
                if (sourceName == null) return@mapNotNull null
                if (recommendedSku.isNullOrEmpty()) return@mapNotNull null

                RecommendsSchemaDto(
                    sourceName = sourceName,
                    excludeSku = excludeSku,
                    recommendedSku = recommendedSku
                )
            }
            .toList()

    }
}