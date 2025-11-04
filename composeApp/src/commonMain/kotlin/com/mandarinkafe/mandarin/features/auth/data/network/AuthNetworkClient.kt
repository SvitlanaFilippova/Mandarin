package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest

interface AuthNetworkClient {
    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response
    suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response
    suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response
}
