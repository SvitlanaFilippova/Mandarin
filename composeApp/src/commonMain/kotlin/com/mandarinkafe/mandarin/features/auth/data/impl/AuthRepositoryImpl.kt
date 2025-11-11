package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.auth.data.api.LocalUserDataCleaner
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorage
import com.mandarinkafe.mandarin.features.auth.data.dto.ActiveSessionsResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.DeleteAccountResponse
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AuthRepositoryImpl(
    private val networkClient: AuthNetworkClient,
    private val tokenStorage: TokenStorage,
    private val userInfoRepository: UserInfoRepository,
    private val localUserDataCleaner: LocalUserDataCleaner,
) : AuthRepository {

    private val _authState = MutableStateFlow(false)
    override val authState: Flow<Boolean> = _authState

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        private const val LOG_TAG = "AuthRepository"
        private const val TOKEN_REFRESH_TAG = "TOKEN REFRESH"
    }

    init {
        // Подписываемся на изменения токенов для синхронизации _authState
        scope.launch {
            tokenStorage.tokensFlow
                .map { it != null }
                .collect { hasTokens ->
                    if (!hasTokens && _authState.value) {
                        // Токены были очищены (например, через interceptor)
                        // Обновляем состояние авторизации
                        _authState.value = false
                        // Очищаем локальные данные пользователя
                        try {
                            localUserDataCleaner.clear()
                            userInfoRepository.clearUserInfo()
                        } catch (e: Exception) {
                            Napier.e("$LOG_TAG: Error clearing local data on token removal", e)
                        }
                    }
                }
        }
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
            // После сохранения токенов валидируем их и загружаем данные пользователя
            val isValid = validateToken()
            if (!isValid) {
                Napier.e("$LOG_TAG: saveTokens - Token validation failed, clearing tokens")
                clearTokens()
                error("Failed to validate tokens after saving")
            }
        } catch (e: IllegalStateException) {
            throw e
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: saveTokens - Exception", e)
            throw e
        }
    }

    private suspend fun clearTokens() {
        try {
            tokenStorage.clearTokens()
            _authState.value = false
            // Очищаем локальные данные пользователя при любой очистке токенов
            // (ручной логаут, невалидные токены, вынужденный логаут и т.д.)
            localUserDataCleaner.clear()
            userInfoRepository.clearUserInfo()
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: clearTokens - Exception", e)
            throw e
        }
    }

    override suspend fun logout() {
        try {
            performServerLogout()
            // Очищаем токены (внутри clearTokens() также очищаются локальные данные)
            clearTokens()
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: Exception during logout", e)
            throw e
        }
    }

    private suspend fun performServerLogout() {
        val accessToken = getAccessToken() ?: return

        try {
            val response = networkClient.logout(accessToken)
            when (response.resultCode) {
                NO_CONNECTION -> Napier.w("$LOG_TAG: No internet during logout, proceeding with local logout")
                else -> Napier.w(
                    "$LOG_TAG: Server logout failed - code ${response.resultCode}, proceeding with local logout"
                )
            }
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: Exception during server logout", e)
        }
    }

    override suspend fun getAccessToken(): String? {
        return try {
            tokenStorage.getTokens()?.accessToken
        } catch (e: Exception) {
            Napier.e("$LOG_TAG.getAccessToken: Exception", e)
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
                    true
                }

                HTTP_SUCCESS -> {
                    _authState.value = true
                    // Обновляем информацию о пользователе из ответа
                    val validateResponse = response as? ValidateTokenResponse
                    validateResponse?.data?.let { userInfoDto ->
                        userInfoRepository.updateFromServer(userInfoDto)
                    }
                    true
                }

                HTTP_UNAUTHORIZED -> {
                    // Токен невалиден - пытаемся обновить через refresh token
                    tryRefreshToken()
                }

                else -> {
                    // Ошибка сервера - считаем токен валидным (оптимистичный сценарий)
                    Napier.e(
                        "$LOG_TAG: validateToken - Server error (code ${response.resultCode}), " +
                                "keeping auth state"
                    )
                    true
                }
            }
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: validateToken - Exception", e)
            // При ошибке считаем токен валидным (оптимистичный сценарий)
            true
        }
    }

    private suspend fun tryRefreshToken(): Boolean {
        return try {
            val tokens = tokenStorage.getTokens()
            if (tokens?.refreshToken == null) {
                Napier.w("$TOKEN_REFRESH_TAG: No refresh token found, clearing auth state")
                clearTokens()
                return false
            }
            val response = networkClient.refreshToken(tokens.refreshToken)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    // При отсутствии сети сохраняем текущее состояние (оптимистичный сценарий)
                    true
                }

                HTTP_SUCCESS -> {
                    val refreshResponse = response as? RefreshTokenResponse
                    val newTokens = refreshResponse?.data?.toDomain()

                    if (newTokens != null) {
                        saveTokens(newTokens)
                        true
                    } else {
                        Napier.e("$TOKEN_REFRESH_TAG: Response is success but data is empty")
                        clearTokens()
                        false
                    }
                }

                HTTP_UNAUTHORIZED -> {
                    // Refresh token тоже невалиден - очищаем всё
                    Napier.w("$TOKEN_REFRESH_TAG: Refresh token is invalid (401), clearing all auth data")
                    clearTokens()
                    false
                }

                else -> {
                    // Ошибка сервера при refresh - не трогаем текущие токены
                    Napier.e(
                        "$TOKEN_REFRESH_TAG: Server error (code ${response.resultCode})," +
                                " keeping current tokens (optimistic)"
                    )
                    true
                }
            }
        } catch (e: Exception) {
            Napier.e(
                "$TOKEN_REFRESH_TAG: Exception occurred, keeping current tokens (optimistic)",
                e
            )
            // При ошибке не трогаем текущие токены
            true
        }
    }

    override suspend fun getActiveSessions(): Resource<List<ActiveSession>> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("$LOG_TAG: getActiveSessions - No access token")
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
                        Napier.e("$LOG_TAG: getActiveSessions - Empty response data")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_UNAUTHORIZED -> {
                    // Интерсептор уже попытался обновить токен, если это 401 - значит refresh token тоже невалиден
                    Napier.e("$LOG_TAG: getActiveSessions - Unauthorized (refresh token invalid)")
                    clearTokens()
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("$LOG_TAG: getActiveSessions - Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("$LOG_TAG: getActiveSessions - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: getActiveSessions - Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun revokeSession(sessionId: String): Resource<Boolean> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("$LOG_TAG: revokeSession - No access token")
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
                    // Интерсептор уже попытался обновить токен, если это 401 - значит refresh token тоже невалиден
                    Napier.e("$LOG_TAG: revokeSession - Unauthorized (refresh token invalid)")
                    clearTokens()
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("$LOG_TAG: revokeSession - Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("$LOG_TAG: revokeSession - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: revokeSession - Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun deleteAccount(): Resource<Boolean> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken == null) {
                Napier.e("$LOG_TAG: deleteAccount - No access token")
                return Resource.ErrorOther("Нет токена авторизации")
            }

            val response = networkClient.deleteAccount(accessToken)

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val wrapper = response as? DeleteAccountResponse
                    val isSuccess = wrapper?.data?.isSuccess ?: false
                    if (isSuccess) {
                        // Очищаем токены и локальные данные после успешного удаления
                        clearTokens()
                    }
                    Resource.Success(isSuccess)
                }

                HTTP_UNAUTHORIZED -> {
                    // Интерсептор уже попытался обновить токен, если это 401 - значит refresh token тоже невалиден
                    Napier.e("$LOG_TAG: deleteAccount - Unauthorized (refresh token invalid)")
                    clearTokens()
                    Resource.ErrorOther("Требуется авторизация")
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("$LOG_TAG: deleteAccount - Server error")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("$LOG_TAG: deleteAccount - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("$LOG_TAG: deleteAccount - Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}

