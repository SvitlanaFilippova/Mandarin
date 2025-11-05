package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeRequest

interface AuthNetworkClient {
    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response
    suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response
    suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response
    suspend fun requestSmsVerification(request: SmsVerificationRequest): Response
    suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response
}
