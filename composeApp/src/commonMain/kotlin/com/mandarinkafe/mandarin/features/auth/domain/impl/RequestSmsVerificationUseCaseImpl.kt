package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestSmsVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.util.Constants.PHONE_PREFIX_RU
import com.mandarinkafe.mandarin.util.Resource

class RequestSmsVerificationUseCaseImpl(private val authRepository: AuthRepository) :
    RequestSmsVerificationUseCase {
    override suspend fun invoke(phone: String): Resource<SmsVerificationData> {
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        return authRepository.requestSmsVerification(phoneWithPrefix)
    }
}