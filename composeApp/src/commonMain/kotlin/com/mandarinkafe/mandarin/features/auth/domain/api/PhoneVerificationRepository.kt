package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface PhoneVerificationRepository {
    // --- Верификация номера через звонок ---
    suspend fun requestPhoneVerification(phone: String): Resource<PhoneVerificationData>
    suspend fun checkVerificationStatusByCheckId(checkId: String): Resource<PhoneVerificationStatus>
    fun observeVerificationStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>>

    // --- Верификация по SMS ---
    suspend fun requestSmsVerification(phone: String): Resource<SmsVerificationData>
    suspend fun verifySmsCode(phone: String, code: String): Resource<VerifySmsCodeResult>
}

