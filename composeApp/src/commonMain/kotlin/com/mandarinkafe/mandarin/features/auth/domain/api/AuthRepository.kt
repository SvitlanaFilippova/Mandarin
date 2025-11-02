package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusRequest
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.util.Resource

interface AuthRepository {
    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Resource<PhoneVerificationData>
    suspend fun checkVerificationStatus(request: PhoneVerificationStatusRequest): Resource<PhoneVerificationStatus>
}

