package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface GoogleDocsNetworkClient {
    suspend fun getBanners(): Response
    suspend fun getRecommendations(): Response
    suspend fun getDeliveryZonesPoints(): Response
    suspend fun getDeliveryZonesMetaData(): Response
}
