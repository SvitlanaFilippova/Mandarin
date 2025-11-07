package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserSessionManager(
    private val authRepository: AuthRepository,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val appScope: CoroutineScope,
) {
    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        appScope.launch {
            authRepository.authState.collectLatest { isAuthorized ->
                if (isAuthorized) {
                    syncUserDataUseCase()
                }
            }
        }
    }

    suspend fun onUserAuthorized(tokens: AuthTokens?) {
        tokens?.let {
            authRepository.saveTokens(tokens)
            syncUserDataUseCase()
        }
    }

    suspend fun logout() {
        authRepository.logout()
        // TODO тут также очищать локальные данные в favoritesRepository, addressesRepository, ordersRepository
    }
}
