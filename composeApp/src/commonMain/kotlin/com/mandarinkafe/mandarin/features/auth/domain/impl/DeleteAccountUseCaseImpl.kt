package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.DeleteAccountUseCase
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class DeleteAccountUseCaseImpl(
    private val authRepository: AuthRepository,
) : DeleteAccountUseCase {

    override suspend fun invoke(): Resource<Boolean> {
        Napier.d("DeleteAccountUseCase.invoke() called")
        val result = authRepository.deleteAccount()
        Napier.d("DeleteAccountUseCase.invoke() returning result")
        return result
    }
}

