package com.mandarinkafe.mandarin.features.more.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.more.data.dto.AppStoresResponse
import com.mandarinkafe.mandarin.features.more.data.toDomain
import com.mandarinkafe.mandarin.features.more.domain.api.AppStoresRepository
import com.mandarinkafe.mandarin.features.more.domain.models.AppStore
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class AppStoresRepositoryImpl(
    private val networkClient: ServerNetworkClient,
) : AppStoresRepository {
    override suspend fun getAppStores(): Resource<List<AppStore>> {
        val response = try {
            networkClient.getAppStores()
        } catch (e: Exception) {
            return Resource.ErrorOther("$ERROR_NETWORK_PREFIX${e.message}")
        }

        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet()
            HTTP_SERVER_ERROR -> Resource.ErrorOther(ERROR_SERVER)
            HTTP_SUCCESS -> {
                val appStoresList = (response as? AppStoresResponse)?.data
                if (appStoresList.isNullOrEmpty()) {
                    Resource.Success(emptyList())
                } else {
                    Resource.Success(appStoresList.map { it.toDomain() })
                }
            }

            else -> Resource.ErrorOther(ERROR_UNKNOWN)
        }
    }

    private companion object {
        private const val ERROR_NETWORK_PREFIX = "Ошибка сети: "
        private const val ERROR_SERVER = "Ошибка сервера"
        private const val ERROR_UNKNOWN = "Неизвестная ошибка"
    }
}

