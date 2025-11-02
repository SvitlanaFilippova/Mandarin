package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class RequestPhoneVerificationUseCaseImpl(
    private val authRepository: AuthRepository,
) : RequestPhoneVerificationUseCase {

    companion object {
        private const val PHONE_PREFIX_RU = "+7"
    }

    override suspend fun invoke(phone: String): Resource<PhoneVerificationData> {
        Napier.d("AUTH DEBUG: RequestPhoneVerificationUseCase.invoke() called with phone: $phone")
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        Napier.d("AUTH DEBUG: Phone with prefix: $phoneWithPrefix")
        val result = authRepository.requestPhoneVerification(
            PhoneVerificationRequest(phone = phoneWithPrefix)
        )
        Napier.d("AUTH DEBUG: RequestPhoneVerificationUseCase.invoke() returning result")
        return result
    }
}

