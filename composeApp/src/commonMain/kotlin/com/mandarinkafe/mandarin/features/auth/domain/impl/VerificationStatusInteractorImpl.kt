package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.VerificationStatusInteractor
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.util.Constants.PHONE_PREFIX_RU
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

class VerificationStatusInteractorImpl(
    private val authRepository: AuthRepository,
) : VerificationStatusInteractor {

    override suspend fun checkByCheckId(checkId: String): Resource<PhoneVerificationStatus> {
        val result = authRepository.checkVerificationStatusByCheckId(checkId)
        return result
    }

    override fun observeStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>> {
        val phoneWithPrefix = "$PHONE_PREFIX_RU$phone"
        return authRepository.observeVerificationStatusByPhone(phoneWithPrefix)
    }

}

