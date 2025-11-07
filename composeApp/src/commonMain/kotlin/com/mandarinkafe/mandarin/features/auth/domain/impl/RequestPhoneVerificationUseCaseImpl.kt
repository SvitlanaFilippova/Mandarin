package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.PhoneVerificationRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.util.Constants.PHONE_PREFIX_RU
import com.mandarinkafe.mandarin.util.Resource

class RequestPhoneVerificationUseCaseImpl(
    private val phoneVerificationRepository: PhoneVerificationRepository,
) : RequestPhoneVerificationUseCase {

    override suspend fun invoke(phone: String): Resource<PhoneVerificationData> {
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        return phoneVerificationRepository.requestPhoneVerification(phone = phoneWithPrefix)
    }
}

