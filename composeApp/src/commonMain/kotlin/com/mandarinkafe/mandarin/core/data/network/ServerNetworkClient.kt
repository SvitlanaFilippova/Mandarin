package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface ServerNetworkClient {
    suspend fun getBanners(): Response
    suspend fun getRecommendations(): Response
    suspend fun getDeliveryZones(): Response
    suspend fun getMenu(): Response
}
