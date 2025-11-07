package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class GetActiveSessionsUseCaseImpl(
    private val authRepository: AuthRepository,
) : GetActiveSessionsUseCase {

    override suspend fun invoke(): Resource<List<ActiveSession>> {
        Napier.d("GetActiveSessionsUseCase.invoke() called")
        val result = authRepository.getActiveSessions()
        Napier.d("GetActiveSessionsUseCase.invoke() returning result")
        return result
    }
}

