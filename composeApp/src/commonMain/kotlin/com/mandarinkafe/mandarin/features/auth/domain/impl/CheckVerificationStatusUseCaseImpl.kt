package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusRequest
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.CheckVerificationStatusUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class CheckVerificationStatusUseCaseImpl(
    private val authRepository: AuthRepository,
) : CheckVerificationStatusUseCase {

    companion object {
        private const val PHONE_PREFIX_RU = "+7"
    }

    override suspend fun invoke(phone: String): Resource<PhoneVerificationStatus> {
        Napier.d("AUTH DEBUG: CheckVerificationStatusUseCase.invoke() called with phone: $phone")
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        Napier.d("AUTH DEBUG: Phone with prefix: $phoneWithPrefix")
        val result = authRepository.checkVerificationStatus(
            PhoneVerificationStatusRequest(phone = phoneWithPrefix)
        )
        Napier.d("AUTH DEBUG: CheckVerificationStatusUseCase.invoke() returning result")
        return result
    }
}

