package com.mandarinkafe.mandarin.features.auth.data.datastore

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getTokens(): AuthTokens?
    suspend fun clearTokens()
    /**
     * Flow для наблюдения за изменениями токенов.
     * Эмитит null, если токены отсутствуют или были очищены.
     */
    val tokensFlow: Flow<AuthTokens?>
}

