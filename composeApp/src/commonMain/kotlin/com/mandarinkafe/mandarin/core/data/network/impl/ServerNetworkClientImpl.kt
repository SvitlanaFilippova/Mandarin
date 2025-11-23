package com.mandarinkafe.mandarin.core.data.network.impl

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION

class ServerNetworkClientImpl(
    private val serverApi: ServerApi,
    private val networkMonitor: NetworkMonitor,
) : ServerNetworkClient {

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun getMenu(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return serverApi.getMenu()
    }

    override suspend fun getBanners(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return serverApi.getBanners()
    }

    override suspend fun getRecommendations(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return serverApi.getRecommendations()
    }

    override suspend fun getDeliveryZones(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return serverApi.getDeliveryZones()
    }

    override suspend fun getPaymentTypes(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return serverApi.getPaymentTypes()
    }
}

