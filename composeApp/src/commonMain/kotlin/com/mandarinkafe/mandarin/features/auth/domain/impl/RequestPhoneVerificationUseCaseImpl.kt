package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.util.Constants.PHONE_PREFIX_RU
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class RequestPhoneVerificationUseCaseImpl(
    private val authRepository: AuthRepository,
) : RequestPhoneVerificationUseCase {

    override suspend fun invoke(phone: String): Resource<PhoneVerificationData> {
        Napier.d("AUTH DEBUG: RequestPhoneVerificationUseCase.invoke() called with phone: $phone")
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        Napier.d("AUTH DEBUG: Phone with prefix: $phoneWithPrefix")
        val result = authRepository.requestPhoneVerification(phone = phoneWithPrefix)
        Napier.d("AUTH DEBUG: RequestPhoneVerificationUseCase.invoke() returning result")
        return result
    }
}

