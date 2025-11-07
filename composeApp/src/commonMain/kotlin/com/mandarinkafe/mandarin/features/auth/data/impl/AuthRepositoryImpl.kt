package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorage
import com.mandarinkafe.mandarin.features.auth.data.dto.ActiveSessionsResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionResponse
import com.mandarinkafe.mandarin.features.auth.data.network.AuthNetworkClient
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepositoryImpl(
    private val networkClient: AuthNetworkClient,
    private val tokenStorage: TokenStorage,
    ) : AuthRepository {

    private val _authState = MutableStateFlow(false)
    override val authState: Flow<Boolean> = _authState

    override suspend fun initializeAuth(): Boolean {
        val hasTokens = tokenStorage.getTokens() != null
        return if (hasTokens) {
            // Если есть токены, валидируем их на сервере
            validateToken()
        } else {
            _authState.value = false
            false
        }
    }

    override fun isAuthorized() = _authState.value

    override suspend fun saveTokens(tokens: AuthTokens) {
        try {
            tokenStorage.saveTokens(tokens)
            _authState.value = true
            Napier.d("Токены успешно сохранены")
        } catch (e: Exception) {
            Napier.e("AuthRepository.saveTokens: Exception", e)
            throw e
        }
    }

    private suspend fun clearTokens() {
        try {
            tokenStorage.clearTokens()
            _authState.value = false
            Napier.d("Токены успешно очищены")
        } catch (e: Exception) {
            Napier.e("AuthRepository.clearTokens: Exception", e)
            throw e
        }
    }

    override suspend fun logout() {
        try {
            Napier.d("AuthRepository.logout: Starting logout process")
            
            // Пытаемся вызвать logout на сервере
            val accessToken = getAccessToken()
            if (accessToken != null) {
                try {
                    val response = networkClient.logout(accessToken)
                    when (response.resultCode) {
                        HTTP_SUCCESS -> {
                            Napier.d("AuthRepository.logout: Server logout successful")
                        }
                        NO_CONNECTION -> {
                            Napier.w("AuthRepository.logout: No internet, proceeding with local logout")
                        }
                        else -> {
                            Napier.w("AuthRepository.logout: Server logout failed with code ${response.resultCode}, proceeding with local logout")
                        }
                    }
                } catch (e: Exception) {
                    Napier.e("AuthRepository.logout: Exception during server logout, proceeding with local logout", e)
                }
            } else {
                Napier.d("AuthRepository.logout: No access token found, skipping server logout")
            }
            
            // Всегда очищаем локальные токены
            clearTokens()
            Napier.d("AuthRepository.logout: Logout complete")
        } catch (e: Exception) {
            Napier.e("AuthRepository.logout: Exception during logout", e)
            throw e
        }
    }

    override suspend fun getAccessToken(): String? {
        return try {
            tokenStorage.getTokens()?.accessToken
        } catch (e: Exception) {
            Napier.e("AuthRepository.getAccessToken: Exception", e)
            null
        }
    }

    override suspend fun validateToken(): Boolean {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                _authState.value = false
                return false
            }

            val response = networkClient.validateToken(accessToken)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    // При отсутствии сети считаем токен валидным (оптимистичный сценарий)
                    Napier.d("AuthRepository.validateToken: No internet, keeping current auth state")
                    true
                }

                HTTP_SUCCESS -> {
                    _authState.value = true
                    Napier.d("AuthRepository.validateToken: Token is valid")
                    true
                }

                401 -> {
                    // Токен невалиден - пытаемся обновить через refresh token
                    Napier.d("AuthRepository.validateToken: Token is invalid (401), trying to refresh")
                    tryRefreshToken()
                }

                else -> {
                    // Ошибка сервера - считаем токен валидным (оптимистичный сценарий)
                    Napier.e("AuthRepository.validateToken: Server error, keeping current auth state")
                    true
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.validateToken: Exception", e)
            // При ошибке считаем токен валидным (оптимистичный сценарий)
            true
        }
    }

    private suspend fun tryRefreshToken(): Boolean {
        return try {
            val tokens = tokenStorage.getTokens()
            if (tokens?.refreshToken == null) {
                Napier.d("AuthRepository.tryRefreshToken: No refresh token, clearing auth")
                clearTokens()
                return false
            }

            val response = networkClient.refreshToken(tokens.refreshToken)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.d("AuthRepository.tryRefreshToken: No internet, keeping current auth state")
                    true
                }

                HTTP_SUCCESS -> {
                    val refreshResponse = response as? RefreshTokenResponse
                    val newTokens = refreshResponse?.data?.toDomain()

                    if (newTokens != null) {
                        saveTokens(newTokens)
                        Napier.d("AuthRepository.tryRefreshToken: Tokens successfully refreshed")
                        true
                    } else {
                        Napier.e("AuthRepository.tryRefreshToken: Empty response data")
                        clearTokens()
                        false
                    }
                }

                401 -> {
                    // Refresh token тоже невалиден - очищаем всё
                    Napier.d("AuthRepository.tryRefreshToken: Refresh token is invalid (401), clearing tokens")
                    clearTokens()
                    false
                }

                else -> {
                    // Ошибка сервера при refresh - не трогаем текущие токены
                    Napier.e("AuthRepository.tryRefreshToken: Server error, keeping current tokens")
                    true
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.tryRefreshToken: Exception", e)
            // При ошибке не трогаем текущие токены
            true
        }
    }

    override suspend fun getActiveSessions(): Resource<List<ActiveSession>> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("AuthRepository.getActiveSessions: No access token")
                return Resource.ErrorOther("Нет токена авторизации")
            }

            val response = networkClient.getActiveSessions(accessToken)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.w("AuthRepository.getActiveSessions: No internet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as? ActiveSessionsResponse
                    val sessions = wrapper?.data?.sessions?.map { it.toDomain() }

                    if (sessions != null) {
                        Napier.d("AuthRepository.getActiveSessions: SUCCESS, sessions count: ${sessions.size}")
                        Resource.Success(sessions)
                    } else {
                        Napier.e("AuthRepository.getActiveSessions: Empty response data")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                401 -> {
                    Napier.e("AuthRepository.getActiveSessions: Unauthorized")
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("AuthRepository.getActiveSessions: Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("AuthRepository.getActiveSessions: Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.getActiveSessions: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun revokeSession(sessionId: String): Resource<Boolean> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("AuthRepository.revokeSession: No access token")
                return Resource.ErrorOther("Нет токена авторизации")
            }

            val response = networkClient.revokeSession(accessToken, RevokeSessionRequest(sessionId))

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.w("AuthRepository.revokeSession: No internet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as? RevokeSessionResponse
                    val success = wrapper?.data?.isSuccess ?: false

                    Napier.d("AuthRepository.revokeSession: SUCCESS, result: $success")
                    Resource.Success(success)
                }

                401 -> {
                    Napier.e("AuthRepository.revokeSession: Unauthorized")
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("AuthRepository.revokeSession: Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("AuthRepository.revokeSession: Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.revokeSession: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}

