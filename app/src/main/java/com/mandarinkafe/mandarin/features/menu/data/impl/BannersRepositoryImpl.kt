package com.mandarinkafe.mandarin.features.menu.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Resource
import java.io.IOException

class BannersRepositoryImpl(
    private val networkClient: NetworkClient,
    private val imageValidator: ImageValidator
) : BannersRepository {
    override suspend fun getBanners(): Resource<List<Banner>> {
        val response = networkClient.getBanners()

        return if (response.resultCode == -1) {
            Resource.Error("Проверьте подключение к интернету")
        } else {
            val csvText = (response as CsvResponse).csv ?: throw IOException("Пустой ответ")

            val bannersDto = parseCsv(csvText)

//      Пока что отключила валидацию картинок по ссылкам, чтобы баннеры быстрее прогружались. Подумать, нужна ли она.
//            val validBanners = coroutineScope {
//                bannersDto.map { banner ->
//                    async {
//                        if (imageValidator.isImageUrlValid(banner.imageUrl)) banner else null
//                    }
//                }.awaitAll().filterNotNull()
//            }

            if (bannersDto.isEmpty()) {
                Resource.Error("Нет валидных баннеров")
            } else {
                Resource.Success(bannersDto.map { it.toDomain() })
            }
        }
    }

    private fun parseCsv(csv: String): List<BannerDto> {
        Log.d("DEBUG googleDocsApi", "parseCsv")
        val lines = csv.trim().lines()
        if (lines.isEmpty()) return emptyList()

        return lines.drop(1).mapNotNull { line ->
            val columns = line.split(",")
            if (columns.size >= 3) {
                BannerDto(
                    imageUrl = columns[0],
                    targetName = columns[1],
                )
            } else null
        }
    }
}
