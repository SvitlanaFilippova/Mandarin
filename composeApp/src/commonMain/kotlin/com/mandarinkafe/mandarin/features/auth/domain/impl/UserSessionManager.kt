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
                syncUserDataUseCase()
            }
        }
    }

    private fun observeAuthState() {
        appScope.launch {
            authRepository.authState.collect { isAuthorized ->
                if (isAuthorized) {
                    syncUserDataUseCase()
                }
            }
        }
    }

    /**
     * Обрабатывает успешную авторизацию пользователя.
     * @return true, если корзина изменилась после синхронизации
     */
    suspend fun onUserAuthorized(tokens: AuthTokens?): Boolean {
        if (tokens == null) return false
        
        authRepository.saveTokens(tokens)
        return syncUserDataUseCase()
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
