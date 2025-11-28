package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.util.Resource

class GetActiveSessionsUseCaseImpl(
    private val authRepository: AuthRepository,
) : GetActiveSessionsUseCase {

    override suspend fun invoke(): Resource<List<ActiveSession>> {
        val result = authRepository.getActiveSessions()
        return result
    }
}






