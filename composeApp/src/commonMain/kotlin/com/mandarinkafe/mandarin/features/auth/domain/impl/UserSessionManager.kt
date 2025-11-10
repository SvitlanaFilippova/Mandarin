package com.mandarinkafe.mandarin.features.auth.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.CartReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserSessionManager(
    private val authRepository: AuthRepository,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val cartReader: CartReader,
    private val favoritesReader: FavoritesReader,
    private val appScope: CoroutineScope,
) {
    init {
        observeAuthState()
        // Проверяем начальное состояние при старте
        appScope.launch {
            if (authRepository.isAuthorized()) {
                Napier.d("UserSessionManager: пользователь уже авторизован при старте, запускаем синхронизацию")
                syncUserDataUseCase()
            }
        }
    }

    private fun observeAuthState() {
        appScope.launch {
            authRepository.authState.collect { isAuthorized ->
                if (isAuthorized) {
                    Napier.d("UserSessionManager: пользователь авторизован, запускаем синхронизацию")
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
        
        // Принудительно обновляем UI после очистки данных
        try {
            cartReader.forceRetry()
        } catch (e: Exception) {
            Napier.e("Ошибка при обновлении UI корзины после логаута", e)
        }
        
        try {
            favoritesReader.forceRetry()
        } catch (e: Exception) {
            Napier.e("Ошибка при обновлении UI избранного после логаута", e)
        }
    }
}
