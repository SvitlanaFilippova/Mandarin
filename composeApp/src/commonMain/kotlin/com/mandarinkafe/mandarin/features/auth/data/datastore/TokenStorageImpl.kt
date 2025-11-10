package com.mandarinkafe.mandarin.features.auth.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class TokenStorageImpl(
    private val dataStore: DataStore<Preferences>,
) : TokenStorage {

    private val json = Json { ignoreUnknownKeys = true }

    override val tokensFlow: Flow<AuthTokens?> = dataStore.data
        .map { prefs ->
            val accessToken = prefs[stringPreferencesKey(ACCESS_TOKEN_KEY)]
            val refreshToken = prefs[stringPreferencesKey(REFRESH_TOKEN_KEY)]
            val tokenType = prefs[stringPreferencesKey(TOKEN_TYPE_KEY)]

            if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
                null
            } else {
                AuthTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    tokenType = tokenType ?: "Bearer"
                )
            }
        }

    override suspend fun getTokens(): AuthTokens? = tokensFlow.first()

    override suspend fun saveTokens(tokens: AuthTokens) {
        try {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(ACCESS_TOKEN_KEY)] = tokens.accessToken
                prefs[stringPreferencesKey(REFRESH_TOKEN_KEY)] = tokens.refreshToken
                prefs[stringPreferencesKey(TOKEN_TYPE_KEY)] = tokens.tokenType
            }
        } catch (e: Exception) {
            Napier.e("TokenStorage.saveTokens: Exception", e)
            throw e
        }
    }

    override suspend fun clearTokens() {
        try {
            dataStore.edit { prefs ->
                prefs.remove(stringPreferencesKey(ACCESS_TOKEN_KEY))
                prefs.remove(stringPreferencesKey(REFRESH_TOKEN_KEY))
                prefs.remove(stringPreferencesKey(TOKEN_TYPE_KEY))
            }
        } catch (e: Exception) {
            Napier.e("TokenStorage.clearTokens: Exception", e)
            throw e
        }
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        const val TOKEN_TYPE_KEY = "TOKEN_TYPE_KEY"
    }
}

