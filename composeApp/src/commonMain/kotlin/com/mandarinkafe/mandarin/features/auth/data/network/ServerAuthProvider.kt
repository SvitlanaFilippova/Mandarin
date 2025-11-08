package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorage
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenRequest
import com.mandarinkafe.mandarin.shared.BuildKonfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Провайдер токенов для Server API.
 * Используется с плагином Auth для автоматического обновления токенов.
 */
class ServerAuthProvider(
    private val tokenStorage: TokenStorage,
    private val refreshTokenClient: HttpClient,
) {
    private val mutex = Mutex()
    private val apiKey = BuildKonfig.MANDARIN_API_KEY

    companion object {
        private const val LOG_PREFIX = "AUTH_INTERCEPTOR"
        private const val REFRESH_TOKEN_PATH = "/auth/refresh_token"
    }

    /**
     * Получает текущий access token из хранилища
     */
    suspend fun getToken(): String {
        return mutex.withLock {
            val tokens = tokenStorage.getTokens()
            if (tokens == null) {
                Napier.e("$LOG_PREFIX ERROR: No tokens in storage")
                error("No access token available")
            }
            val accessToken = tokens.accessToken
            if (accessToken == null) {
                Napier.e("$LOG_PREFIX ERROR: Access token is null in storage")
                error("No access token available")
            }
            Napier.d(
                "$LOG_PREFIX DEBUG: Token loaded, length=${accessToken.length}, prefix=${
                    accessToken.take(
                        20
                    )
                }..."
            )
            accessToken
        }
    }

    /**
     * Обновляет токен через refresh token API
     */
    suspend fun refreshToken(): String {
        return mutex.withLock {
            Napier.d("$LOG_PREFIX DEBUG: Starting token refresh...")
            val tokens = tokenStorage.getTokens()
            val refreshToken = tokens?.refreshToken
            if (refreshToken == null) {
                Napier.e("$LOG_PREFIX ERROR: No refresh token found")
                tokenStorage.clearTokens()
                error("No refresh token available")
            }

            Napier.d("$LOG_PREFIX DEBUG: Sending refresh request to $REFRESH_TOKEN_PATH")
            val response = refreshTokenClient.post(REFRESH_TOKEN_PATH) {
                header("x-api-key", apiKey)
                setBody(RefreshTokenRequest(refreshToken))
            }

            Napier.d("$LOG_PREFIX DEBUG: Refresh response status: ${response.status.value}")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val refreshData: com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenDataDto =
                        response.body()
                    val newTokens = AuthTokens(
                        accessToken = refreshData.accessToken,
                        refreshToken = refreshData.refreshToken,
                        tokenType = refreshData.tokenType
                    )

                    tokenStorage.saveTokens(newTokens)
                    Napier.d("$LOG_PREFIX SUCCESS: Token refreshed successfully, new token length=${newTokens.accessToken.length}")
                    newTokens.accessToken
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.e("$LOG_PREFIX ERROR: Refresh token is invalid (401), clearing tokens")
                    tokenStorage.clearTokens()
                    error("Token refresh failed: ${response.status.value}")
                }

                else -> {
                    Napier.e("$LOG_PREFIX ERROR: Server error during refresh: ${response.status.value}")
                    error("Token refresh failed: ${response.status.value}")
                }
            }
        }
    }
}

