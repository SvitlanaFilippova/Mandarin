package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class RevokeSessionUseCaseImpl(
    private val authRepository: AuthRepository,
) : RevokeSessionUseCase {

    override suspend fun invoke(sessionId: String): Resource<Boolean> {
        Napier.d("RevokeSessionUseCase.invoke() called with sessionId: $sessionId")
        val result = authRepository.revokeSession(sessionId)
        Napier.d("RevokeSessionUseCase.invoke() returning result")
        return result
    }
}

