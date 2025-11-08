package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.PhoneVerificationRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.VerificationStatusInteractor
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.util.Constants.PHONE_PREFIX_RU
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

class VerificationStatusInteractorImpl(
    private val phoneVerificationRepository: PhoneVerificationRepository,
) : VerificationStatusInteractor {

    override suspend fun checkByCheckId(checkId: String): Resource<PhoneVerificationStatus> {
        val result = phoneVerificationRepository.checkVerificationStatusByCheckId(checkId = checkId)
        return result
    }

    override fun observeStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>> {
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        return phoneVerificationRepository.observeVerificationStatusByPhone(phone = phoneWithPrefix)
    }

    override suspend fun checkSms(
        phone: String,
        code: String,
    ): Resource<VerifySmsCodeResult> {
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        return phoneVerificationRepository.verifySmsCode(phone = phoneWithPrefix, code = code)
    }
}

