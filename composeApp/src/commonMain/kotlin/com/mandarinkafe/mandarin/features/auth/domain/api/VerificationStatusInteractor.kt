package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface VerificationStatusInteractor {
    suspend fun checkByCheckId(checkId: String): Resource<PhoneVerificationStatus>
    fun observeStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>>
    suspend fun checkSms(phone: String, code: String): Resource<VerifySmsCodeResult>
}

