package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZonesResponse
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendationsResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.BannersResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierGroupsResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.ServerMenuResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class ServerApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getMenu(): Response {
        return try {
            val response: ServerMenuResponse = client.get("/menu") {
                header("x-api-key", key)
            }.body()
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("ServerApi: getMenu(): ошибка получения меню", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getBanners(): Response {
        return try {
            val bannersList: List<com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto> =
                client.get("/banners") {}.body()
            BannersResponse(data = bannersList).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("ServerApi: getBanners(): ошибка получения баннеров", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getRecommendations(): Response {
        return try {
            val recommendsList: List<com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto> =
                client.get("/recommendations") {}.body()
            RecommendationsResponse(data = recommendsList).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("ServerApi: getRecommendations(): ошибка получения рекомендаций", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getDeliveryZones(): Response {
        return try {
            val deliveryZones: List<com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZoneDto> =
                client.get("/delivery_zones/") {}.body()
            DeliveryZonesResponse(data = deliveryZones).apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("ServerApi: getDeliveryZones(): ошибка получения зон доставки", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getModifierGroups(): Response {
        return try {
            val response: ModifierGroupsResponse =
                client.get("/modifier-groups") {
                    header("x-api-key", key)
                }.body<ModifierGroupsResponse>()
            response.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Napier.e("ServerApi: getModifierGroups(): ошибка получения групп модификаторов", e)
            ModifierGroupsResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}
