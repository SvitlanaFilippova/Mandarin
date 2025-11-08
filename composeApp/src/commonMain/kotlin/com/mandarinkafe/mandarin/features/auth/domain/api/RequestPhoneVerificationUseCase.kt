package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.util.Resource

interface RequestPhoneVerificationUseCase {
    suspend operator fun invoke(phone: String): Resource<PhoneVerificationData>
}

