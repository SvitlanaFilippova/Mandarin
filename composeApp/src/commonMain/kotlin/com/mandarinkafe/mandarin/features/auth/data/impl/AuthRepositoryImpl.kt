package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorage
import com.mandarinkafe.mandarin.features.auth.data.dto.ActiveSessionsResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.ValidateTokenResponse
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
    private val userInfoRepository: UserInfoRepository,
) : AuthRepository {

    private val _authState = MutableStateFlow(false)
    override val authState: Flow<Boolean> = _authState

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
    }

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
            Napier.d("AuthRepository: saveTokens - Tokens saved, validating...")
            // После сохранения токенов валидируем их и загружаем данные пользователя
            val isValid = validateToken()
            if (!isValid) {
                Napier.e("AuthRepository: saveTokens - Token validation failed, clearing tokens")
                clearTokens()
                throw IllegalStateException("Failed to validate tokens after saving")
            }
            Napier.d("AuthRepository: saveTokens - ✅ Tokens saved and validated successfully")
        } catch (e: Exception) {
            if (e !is IllegalStateException) {
                Napier.e("AuthRepository: saveTokens - Exception", e)
            }
            throw e
        }
    }

    private suspend fun clearTokens() {
        try {
            tokenStorage.clearTokens()
            _authState.value = false
        } catch (e: Exception) {
            Napier.e("AuthRepository: clearTokens - Exception", e)
            throw e
        }
    }

    override suspend fun logout() {
        try {
            // Пытаемся вызвать logout на сервере
            val accessToken = getAccessToken()
            if (accessToken != null) {
                try {
                    val response = networkClient.logout(accessToken)
                    when (response.resultCode) {
                        HTTP_SUCCESS -> {
                            Napier.d("AuthRepository: Logout successful on server")
                        }

                        NO_CONNECTION -> {
                            Napier.w("AuthRepository: No internet during logout, proceeding with local logout")
                        }

                        else -> {
                            Napier.w(
                                "AuthRepository: Server logout failed (code ${response.resultCode}), " +
                                        "proceeding with local logout"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Napier.e("AuthRepository: Exception during server logout", e)
                }
            }

            // Всегда очищаем локальные токены и данные пользователя
            clearTokens()
            userInfoRepository.clearUserInfo()
        } catch (e: Exception) {
            Napier.e("AuthRepository: Exception during logout", e)
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
                    _authState.value = true
                    Napier.d("AuthRepository: validateToken - No connection, assuming tokens are valid")
                    true
                }

                HTTP_SUCCESS -> {
                    _authState.value = true
                    // Обновляем информацию о пользователе из ответа
                    val validateResponse = response as? ValidateTokenResponse
                    validateResponse?.data?.let { userInfoDto ->
                        Napier.d("AuthRepository: validateToken - Updating user info from server")
                        userInfoRepository.updateFromServer(userInfoDto)
                    }
                    true
                }

                HTTP_UNAUTHORIZED -> {
                    // Токен невалиден - пытаемся обновить через refresh token
                    Napier.d("AuthRepository: Token invalid (401), attempting refresh")
                    tryRefreshToken()
                }

                else -> {
                    // Ошибка сервера - считаем токен валидным (оптимистичный сценарий)
                    Napier.e(
                        "AuthRepository: validateToken - Server error (code ${response.resultCode}), " +
                                "keeping auth state"
                    )
                    true
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository: validateToken - Exception", e)
            // При ошибке считаем токен валидным (оптимистичный сценарий)
            true
        }
    }

    private suspend fun tryRefreshToken(): Boolean {
        return try {
            Napier.d("=== TOKEN REFRESH: Started ===")
            val tokens = tokenStorage.getTokens()
            if (tokens?.refreshToken == null) {
                Napier.w("TOKEN REFRESH: No refresh token found, clearing auth state")
                clearTokens()
                return false
            }
            val response = networkClient.refreshToken(tokens.refreshToken)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.w("TOKEN REFRESH: No internet connection, keeping current auth state (optimistic)")
                    true
                }

                HTTP_SUCCESS -> {
                    Napier.d("TOKEN REFRESH: Server responded with success")
                    val refreshResponse = response as? RefreshTokenResponse
                    val newTokens = refreshResponse?.data?.toDomain()

                    if (newTokens != null) {
                        saveTokens(newTokens)
                        true
                    } else {
                        Napier.e("TOKEN REFRESH: ❌ Response is success but data is empty")
                        clearTokens()
                        false
                    }
                }

                HTTP_UNAUTHORIZED -> {
                    // Refresh token тоже невалиден - очищаем всё
                    Napier.w("TOKEN REFRESH: ❌ Refresh token is invalid (401), clearing all auth data")
                    clearTokens()
                    false
                }

                else -> {
                    // Ошибка сервера при refresh - не трогаем текущие токены
                    Napier.e(
                        "TOKEN REFRESH: Server error (code ${response.resultCode})," +
                                " keeping current tokens (optimistic)"
                    )
                    true
                }
            }
        } catch (e: Exception) {
            Napier.e("TOKEN REFRESH: ❌ Exception occurred, keeping current tokens (optimistic)", e)
            // При ошибке не трогаем текущие токены
            true
        }
    }

    override suspend fun getActiveSessions(): Resource<List<ActiveSession>> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("AuthRepository: getActiveSessions - No access token")
                return Resource.ErrorOther("Нет токена авторизации")
            }

            val response = networkClient.getActiveSessions(accessToken)

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val wrapper = response as? ActiveSessionsResponse
                    val sessions = wrapper?.data?.sessions?.map { it.toDomain() }

                    if (sessions != null) {
                        Resource.Success(sessions)
                    } else {
                        Napier.e("AuthRepository: getActiveSessions - Empty response data")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_UNAUTHORIZED -> {
                    Napier.e("AuthRepository: getActiveSessions - Unauthorized")
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("AuthRepository: getActiveSessions - Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("AuthRepository: getActiveSessions - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository: getActiveSessions - Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun revokeSession(sessionId: String): Resource<Boolean> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("AuthRepository: revokeSession - No access token")
                return Resource.ErrorOther("Нет токена авторизации")
            }

            val response = networkClient.revokeSession(accessToken, RevokeSessionRequest(sessionId))

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val wrapper = response as? RevokeSessionResponse
                    Resource.Success(wrapper?.data?.isSuccess ?: false)
                }

                HTTP_UNAUTHORIZED -> {
                    Napier.e("AuthRepository: revokeSession - Unauthorized")
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("AuthRepository: revokeSession - Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("AuthRepository: revokeSession - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository: revokeSession - Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}

