package com.mandarinkafe.mandarin.core.data.network.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.features.address.address.data.dto.DeliveryZonesResponse
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendationsResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.BannersResponse
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServerNetworkClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val serverApi: ServerApi
) : ServerNetworkClient {

    private val logTag = "ServerNetworkClient Debug"

    private fun isConnected(): Boolean = networkMonitor.isNetworkAvailable()

    override suspend fun getMenu(): Response {
        Log.d(logTag, "getMenu(): старт запроса")
        if (!isConnected()) {
            Log.w(logTag, "getMenu(): нет подключения к сети")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return withContext(Dispatchers.IO) {
            try {
                val menuResponse = serverApi.getMenu()
                Log.d(logTag, "getMenu(): получен ответ")
                menuResponse.apply { resultCode = HTTP_SUCCESS }
            } catch (e: Throwable) {
                Log.e(logTag, "getMenu(): ошибка при получении меню", e)
                Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        }
    }

    override suspend fun getBanners(): Response {
        Log.d(logTag, "getBanners(): старт запроса")
        if (!isConnected()) {
            Log.w(logTag, "getBanners(): нет подключения к сети")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val bannersList = serverApi.getBanners()
            Log.d(logTag, "getBanners(): получено баннеров=${bannersList.size}")
            BannersResponse(data = bannersList).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "getBanners(): ошибка получения баннеров", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getRecommendations(): Response {
        Log.d(logTag, "getRecommendations(): старт запроса")
        if (!isConnected()) {
            Log.w(logTag, "getRecommendations(): нет подключения к сети")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val recommendsList = serverApi.getRecommendations()
            Log.d(logTag, "getRecommendations(): получено элементов=${recommendsList.size}")
            RecommendationsResponse(data = recommendsList).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "getRecommendations(): ошибка получения рекомендаций", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getDeliveryZones(): Response {
        Log.d(logTag, "getDeliveryZones(): старт запроса")
        if (!isConnected()) {
            Log.w(logTag, "getDeliveryZones(): нет подключения к сети")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val deliveryZones = serverApi.getDeliveryZones()
            Log.d(logTag, "getDeliveryZones(): получено зон=${deliveryZones.size}")
            DeliveryZonesResponse(data = deliveryZones).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.e(logTag, "getDeliveryZones(): ошибка получения зон доставки", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}
