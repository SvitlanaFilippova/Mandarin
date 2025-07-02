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

    var bannersCsv: String? = null

    /** Загрузка и кэширование CSV  */
    override suspend fun loadBannersCsv(): Resource<Unit> {
        val response = try {
            networkClient.getBanners()
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }

        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }

        val csv = (response as? CsvResponse)?.csv
        if (csv.isNullOrEmpty()) {
            return Resource.ErrorEmptyData()
        }

        bannersCsv = csv
        return Resource.Success(Unit)
    }

    /** Парсинг и валидация баннеров */
    override suspend fun getBanners(): Resource<List<Banner>> {
        // Загружаем CSV, если ещё не загружен
        if (bannersCsv.isNullOrEmpty()) {
            val csvResult = loadBannersCsv()

            val error = when (csvResult) {
                is Resource.Success -> null
                is Resource.ErrorNoInternet -> Resource.ErrorNoInternet<List<Banner>>()
                is Resource.ErrorEmptyData -> Resource.ErrorEmptyData<List<Banner>>()
                is Resource.ErrorOther -> Resource.ErrorOther<List<Banner>>(csvResult.message.orEmpty())
                else -> Resource.ErrorOther("Неизвестная ошибка при загрузке баннеров")
            }
            if (error != null) return error
        }

        val csv = bannersCsv
        if (csv.isNullOrEmpty()) {
            return Resource.ErrorOther("CSV не загружен")
        }

        val bannersDto = runCatching { parseCsv(csv) }
            .getOrElse { return Resource.ErrorOther("Ошибка разбора CSV: ${it.message}") }

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

        return if (validBanners.isEmpty()) {
            Resource.ErrorEmptyData()
        } else {
            Resource.Success(validBanners)
        }
    }

    private fun parseCsv(csv: String): List<BannerDto> {
        val lines = csv.lineSequence()
            .filter { it.isNotBlank() }
            .toList()
        if (lines.size <= 1) return emptyList() // только заголовок

        return lines.drop(1).mapNotNull { line ->
            val cols = line.split(",", limit = 2)
            if (cols.isEmpty()) return@mapNotNull null

            val imageUrl = cols[0].trim()
            if (imageUrl.isEmpty()) return@mapNotNull null

            val targetName = cols.getOrNull(1)?.trim().orEmpty()
            BannerDto(imageUrl = imageUrl, targetName = targetName)
        }
    }
}
