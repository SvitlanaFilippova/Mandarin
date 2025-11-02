package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.util.Resource

interface CheckVerificationStatusUseCase {
    suspend operator fun invoke(phone: String): Resource<PhoneVerificationStatus>
}

