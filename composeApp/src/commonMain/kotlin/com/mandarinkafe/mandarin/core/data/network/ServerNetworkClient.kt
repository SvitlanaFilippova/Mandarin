package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusRequest

interface ServerNetworkClient {
    suspend fun getBanners(): Response
    suspend fun getRecommendations(): Response
    suspend fun getDeliveryZones(): Response
    suspend fun getMenu(): Response
    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response
    suspend fun checkVerificationStatus(request: PhoneVerificationStatusRequest): Response
}
