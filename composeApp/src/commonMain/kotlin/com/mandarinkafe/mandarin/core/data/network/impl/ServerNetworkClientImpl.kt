package com.mandarinkafe.mandarin.core.data.network.impl

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZonesResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusResponse
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendationsResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.BannersResponse
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import io.github.aakira.napier.Napier

class ServerNetworkClientImpl(
    private val serverApi: ServerApi,
    private val networkMonitor: NetworkMonitor,
) : ServerNetworkClient {

    private val logTag = "ServerApi"

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun getMenu(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val menuResponse = serverApi.getMenu()
            menuResponse.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("$logTag: getMenu(): ошибка получения меню", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getBanners(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val bannersList = serverApi.getBanners()
            BannersResponse(data = bannersList).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("$logTag: getBanners(): ошибка получения баннеров", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getRecommendations(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val recommendsList = serverApi.getRecommendations()
            RecommendationsResponse(data = recommendsList).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("$logTag: getRecommendations(): ошибка получения рекомендаций", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun getDeliveryZones(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val deliveryZones = serverApi.getDeliveryZones()
            DeliveryZonesResponse(data = deliveryZones).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("$logTag: getDeliveryZones(): ошибка получения зон доставки", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response {
        Napier.d("AUTH DEBUG: ServerNetworkClient.requestPhoneVerification() called with phone: ${request.phone}")
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            Napier.d("AUTH DEBUG: Calling serverApi.requestPhoneVerification()")
            val response = serverApi.requestPhoneVerification(request)
            Napier.d("AUTH DEBUG: ServerApi returned success, creating PhoneVerificationResponse")
            PhoneVerificationResponse(data = response).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.d("AUTH DEBUG: Exception in requestPhoneVerification: ${e.message}")
            Napier.e("$logTag: requestPhoneVerification(): ошибка запроса верификации", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    override suspend fun checkVerificationStatus(request: PhoneVerificationStatusRequest): Response {
        Napier.d("AUTH DEBUG: ServerNetworkClient.checkVerificationStatus() called with phone: ${request.phone}")
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            Napier.d("AUTH DEBUG: Calling serverApi.checkVerificationStatus()")
            val response = serverApi.checkVerificationStatus(request)
            Napier.d("AUTH DEBUG: ServerApi returned success, creating PhoneVerificationStatusResponse")
            PhoneVerificationStatusResponse(data = response).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.d("AUTH DEBUG: Exception in checkVerificationStatus: ${e.message}")
            Napier.e("$logTag: checkVerificationStatus(): ошибка проверки статуса", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}

