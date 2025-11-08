package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository

class AuthInteractor(
    private val authRepository: AuthRepository,
) {
    fun isAuthorizedFast(): Boolean {
        return authRepository.isAuthorized()
    }
}