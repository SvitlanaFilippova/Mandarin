package com.mandarinkafe.mandarin.features.auth.data.datastore

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens

interface TokenStorage {
    suspend fun saveTokens(tokens: AuthTokens)
    suspend fun getTokens(): AuthTokens?
    suspend fun clearTokens()
}

