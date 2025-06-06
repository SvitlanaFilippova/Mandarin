package com.mandarinkafe.mandarin.features.menu.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class BannersRepositoryImpl(
    private val networkClient: NetworkClient,
    private val imageValidator: ImageValidator
) : BannersRepository {

    override suspend fun getBanners(): Resource<List<Banner>> {
        val response = try {
            networkClient.getBanners()
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }

        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }

        val csvText = (response as? CsvResponse)?.csv

        if (csvText.isNullOrEmpty()) {
            return Resource.ErrorEmptyData()
        }

        val bannersDto = try {
            parseCsv(csvText)
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка разбора CSV: ${e.message}")
        }

        if (bannersDto.isEmpty()) {
            Log.e("DEBUG BannersRepo", "getBanners(): no valid banners")
            return Resource.ErrorEmptyData()
        }

        val domain = bannersDto.map { it.toDomain() }
        val validBanners = coroutineScope {
            domain.map { banner ->
                async {
                    if (imageValidator.isImageUrlValid(banner.imageUrl)) banner else null
                }
            }.awaitAll().filterNotNull()
        }
        return if (validBanners.isEmpty()) Resource.ErrorEmptyData() else
            Resource.Success(validBanners)
    }

    private fun parseCsv(csv: String): List<BannerDto> {
        val lines = csv
            .lineSequence()
            .filter { it.isNotBlank() }
            .toList()
        if (lines.size <= 1) return emptyList()  // нет данных

        // пропускаем заголовок
        return lines
            .drop(1)
            .mapNotNull { line ->
                // разбиваем только на 2 части: imageUrl и остальное
                val cols = line.split(",", limit = 2)
                if (cols.isEmpty()) {
                    return@mapNotNull null
                }

                val imageUrl = cols[0].trim()
                if (imageUrl.isEmpty()) {
                    return@mapNotNull null
                }

                // targetName может быть пустым или отсутствовать
                val targetName = cols.getOrNull(1)?.trim().orEmpty()
                BannerDto(
                    imageUrl = imageUrl,
                    targetName = targetName
                )
            }
    }
}
