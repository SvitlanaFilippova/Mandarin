package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface ServerNetworkClient {
    suspend fun getBanners(): Response
    suspend fun getAnnouncements(): Response
    suspend fun getOrderAcceptStatus(): Response
    suspend fun getRecommendations(): Response
    suspend fun getDeliveryZones(): Response
    suspend fun getMenu(): Response
    suspend fun getPaymentTypes(): Response
    suspend fun getAppStores(): Response
}
