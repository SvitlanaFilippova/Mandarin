package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.address.data.dto.DeliveryZoneDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusRequest
import com.mandarinkafe.mandarin.features.cart.data.dto.RecommendsSchemaDto
import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ServerMenuResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

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

    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): PhoneVerificationDataDto {
        Napier.d("AUTH DEBUG: Sending POST /auth/request with phone: ${request.phone}")
        val httpResponse = client.post("/auth/request") {
            header("x-api-key", key)
            setBody(request)
        }

        when (httpResponse.status) {
            HttpStatusCode.OK -> {
                val response: PhoneVerificationDataDto = httpResponse.body()
                Napier.d("AUTH DEBUG: Received response from /auth/request, checkId: ${response.checkId}, phoneToCall: ${response.phoneToCall}")
                return response
            }

            else -> {
                Napier.d("AUTH DEBUG: HTTP error status: ${httpResponse.status.value}")
                throw Exception("HTTP ${httpResponse.status.value}: ${httpResponse.status.description}")
            }
        }
    }

    suspend fun checkVerificationStatus(request: PhoneVerificationStatusRequest): PhoneVerificationStatusDto {
        Napier.d("AUTH DEBUG: Sending POST /auth/verify-status with phone: ${request.phone}")
        val httpResponse = client.post("/auth/verify-status") {
            header("x-api-key", key)
            setBody(request)
        }

        when (httpResponse.status) {
            HttpStatusCode.OK -> {
                val response: PhoneVerificationStatusDto = httpResponse.body()
                Napier.d("AUTH DEBUG: Received response from /auth/verify-status, isVerified: ${response.isVerified}, shouldStopPolling: ${response.shouldStopPolling}, expiresInSeconds: ${response.expiresInSeconds}")
                return response
            }

            else -> {
                Napier.d("AUTH DEBUG: HTTP error status: ${httpResponse.status.value}")
                throw Exception("HTTP ${httpResponse.status.value}: ${httpResponse.status.description}")
            }
        }
    }
}
