package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZoneDto
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ServerMenuResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class ServerApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getMenu(): ServerMenuResponse {
        return client.get("/menu") {
            header("x-api-key", key)
        }.body()
    }

    suspend fun getBanners(): List<BannerDto> {
        return client.get("/banners") {}.body()
    }

    suspend fun getRecommendations(): List<RecommendsSchemaDto> {
        return client.get("/recommendations") {}.body()
    }

    suspend fun getDeliveryZones(): List<DeliveryZoneDto> {
        return client.get("/delivery_zones/") {
        }.body()
    }
}
