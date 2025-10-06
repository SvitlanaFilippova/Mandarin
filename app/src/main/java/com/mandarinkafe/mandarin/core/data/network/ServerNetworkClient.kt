package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface ServerNetworkClient {
    suspend fun getMenu(): Response
    suspend fun getBanners(): Response
    suspend fun getRecommendations(): Response
    suspend fun getDeliveryZones(): Response
}