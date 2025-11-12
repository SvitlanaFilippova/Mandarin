package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.DeleteAccountUseCase
import com.mandarinkafe.mandarin.util.Resource

class DeleteAccountUseCaseImpl(
    private val authRepository: AuthRepository,
) : DeleteAccountUseCase {

    override suspend fun invoke(): Resource<Boolean> {
        val result = authRepository.deleteAccount()
        return result
    }
}

