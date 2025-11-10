package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.util.Resource

interface RequestSmsVerificationUseCase {
    suspend operator fun invoke(phone: String): Resource<SmsVerificationData>
}