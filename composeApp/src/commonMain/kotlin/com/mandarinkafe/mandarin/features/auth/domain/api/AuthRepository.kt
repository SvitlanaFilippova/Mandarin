package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // --- Токены ---
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun validateToken(): Boolean // проверяет токен на сервере и обновляет authState

    // --- Авторизация ---
    suspend fun initializeAuth(): Boolean // проверяет и валидирует токены при старте приложения
    val authState: Flow<Boolean> // реактивный поток для UI
    fun isAuthorized(): Boolean // разовая проверка авторизация, без проверки валидности токена
    suspend fun getAccessToken(): String? // возвращает null, если токен не найден
    suspend fun logout() // выход из системы с вызовом API и очисткой токенов

    // --- Сессии ---
    suspend fun getActiveSessions(): Resource<List<ActiveSession>>
    suspend fun revokeSession(sessionId: String): Resource<Boolean>
}
