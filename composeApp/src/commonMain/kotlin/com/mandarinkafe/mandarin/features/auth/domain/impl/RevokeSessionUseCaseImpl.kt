package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
import com.mandarinkafe.mandarin.util.Resource

class RevokeSessionUseCaseImpl(
    private val authRepository: AuthRepository,
) : RevokeSessionUseCase {

    override suspend fun invoke(sessionId: String): Resource<Boolean> {
        val result = authRepository.revokeSession(sessionId)
        return result
    }
}





