package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // --- Верификация номера ---
    suspend fun requestPhoneVerification(phone: String): Resource<PhoneVerificationData>
    suspend fun checkVerificationStatusByCheckId(checkId: String): Resource<PhoneVerificationStatus>
    fun observeVerificationStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>>

    // --- Верификация по SMS ---
    suspend fun requestSmsVerification(phone: String): Resource<SmsVerificationData>
    suspend fun verifySmsCode(phone: String, code: String): Resource<VerifySmsCodeResult>

    // --- Токены ---
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun clearTokens()

    // --- Авторизация ---
    val authState: Flow<Boolean>       // реактивный поток для UI
    suspend fun isAuthorized(): Boolean // разовая проверка
    suspend fun getAccessToken(): String? // возвращает null, если токен невалиден или не найден
}
