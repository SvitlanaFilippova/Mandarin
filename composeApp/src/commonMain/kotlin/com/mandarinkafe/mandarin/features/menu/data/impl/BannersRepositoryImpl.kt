package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.features.menu.data.dto.BannersResponse
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class BannersRepositoryImpl(
    private val networkClient: ServerNetworkClient,
    private val imageValidator: ImageValidator,
) : BannersRepository {

    private var bannersCache: List<Banner>? = null

    /** Загрузка и кэширование баннеров из API */
    override suspend fun loadBanners(): Resource<Unit> {
        val response = try {
            networkClient.getBanners()
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }

        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }

        val bannersDtoList = (response as? BannersResponse)?.data
        if (bannersDtoList.isNullOrEmpty()) {
            return Resource.ErrorEmptyData()
        }

        bannersCache = bannersDtoList.map { it.toDomain() }
        return Resource.Success(Unit)
    }

    /** Получение баннеров с проверкой кэша и валидностью изображений */
    override suspend fun getBanners(): Resource<List<Banner>> {
        if (bannersCache.isNullOrEmpty()) {
            val result = loadBanners()
            if (result !is Resource.Success) {
                return when (result) {
                    is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                    is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
                    is Resource.ErrorOther -> Resource.ErrorOther(result.message.orEmpty())
                    else -> Resource.ErrorOther("Неизвестная ошибка при загрузке баннеров")
                }
            }
        }

        val validBanners = coroutineScope {
            bannersCache!!.map { banner ->
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
}
