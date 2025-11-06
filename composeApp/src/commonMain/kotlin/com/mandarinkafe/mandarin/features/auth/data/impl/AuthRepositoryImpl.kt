package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.core.domain.models.AuthTokens
import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorage
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeResponse
import com.mandarinkafe.mandarin.features.auth.data.network.AuthNetworkClient
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

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

    override suspend fun isAuthorized() = _authState.value


    override suspend fun requestPhoneVerification(phone: String): Resource<PhoneVerificationData> {
        return try {
            val response = networkClient.requestPhoneVerification(PhoneVerificationRequest(phone))
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationResponse
                    wrapper.data?.let {
                        val domainData = it.toDomain()
                        Resource.Success(domainData)
                    } ?: Resource.ErrorOther("Пустой ответ от сервера")
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.requestPhoneVerification: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    // ручная проверка статуса, не дожидаясь webhook от сервиса
    override suspend fun checkVerificationStatusByCheckId(checkId: String): Resource<PhoneVerificationStatus> {
        return try {
            val response = networkClient.checkVerificationStatusByCheckId(
                PhoneVerificationStatusByCheckIdRequest(checkId)
            )
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationStatusResponse
                    wrapper.data?.let {
                        val domainStatus = it.toDomain()
                        Resource.Success(domainStatus)
                    } ?: Resource.ErrorOther("Пустой ответ от сервера")
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.checkVerificationStatusByCheckId: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    // ждём подтверждение по webhook
    override fun observeVerificationStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>> =
        flow {
            val request = PhoneVerificationStatusByPhoneRequest(phone = phone)

            while (true) {
                try {
                    val response = checkVerificationStatusByPhone(request)
                    emit(response)

                    when (response) {
                        is Resource.Success -> {
                            val status = response.data

                            if (status == null) {
                                emit(Resource.ErrorOther("Пустой ответ от сервера"))
                                delay(POLLING_INTERVAL_SLOW_MS)
                            } else {
                                // Останавливаем пулинг, если нужно
                                if (status.shouldStopPolling == true) {
                                    break
                                }

                                // Определяем интервал пулинга на основе оставшегося времени
                                val expiresIn = status.expiresInSeconds
                                if (expiresIn == null || expiresIn <= 0) {
                                    break
                                }

                                val pollingInterval = when {
                                    // Первые 60 секунд - часто
                                    expiresIn > SECONDS_TO_CALL_DEFAULT - FAST_POLLING_START_SECONDS -> {
                                        POLLING_INTERVAL_FAST_MS
                                    }
                                    // Последние 30 секунд - часто
                                    expiresIn <= FAST_POLLING_END_SECONDS -> {
                                        POLLING_INTERVAL_FAST_MS
                                    }
                                    // Средний период - реже
                                    else -> {
                                        POLLING_INTERVAL_MEDIUM_MS
                                    }
                                }

                                delay(pollingInterval)
                            }
                        }

                        is Resource.ErrorNoInternet -> {
                            delay(POLLING_INTERVAL_MEDIUM_MS)
                        }

                        else -> {
                            delay(POLLING_INTERVAL_SLOW_MS)
                        }
                    }
                } catch (e: Exception) {
                    Napier.e("AuthRepository.observeVerificationStatusByPhone: Exception", e)
                    emit(Resource.ErrorOther("Ошибка: ${e.message}"))
                    delay(POLLING_INTERVAL_SLOW_MS)
                }
            }
        }

    private suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Resource<PhoneVerificationStatus> {
        return try {
            val response = networkClient.checkVerificationStatusByPhone(request)
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationStatusResponse
                    wrapper.data?.let {
                        val domainStatus = it.toDomain()
                        Resource.Success(domainStatus)
                    } ?: Resource.ErrorOther("Пустой ответ от сервера")
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.checkVerificationStatusByPhone: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun requestSmsVerification(phone: String): Resource<SmsVerificationData> {
        return try {
            val response = networkClient.requestSmsVerification(SmsVerificationRequest(phone))
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as SmsVerificationResponse
                    wrapper.data?.let { dto ->
                        val domainData = dto.toDomain()
                        Resource.Success(domainData)
                    } ?: Resource.ErrorOther("Пустой ответ от сервера")
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Resource.ErrorOther("Неизвестная ошибка (код: ${response.resultCode})")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.requestSmsVerification: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    // попытка подтверждения кодом из SMS
    override suspend fun verifySmsCode(phone: String, code: String): Resource<VerifySmsCodeResult> {
        return try {
            val response = networkClient.verifySmsCode(VerifySmsCodeRequest(phone, code))
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as VerifySmsCodeResponse
                    wrapper.data?.let { dto ->
                        val domainResult = dto.toDomain()
                        Resource.Success(domainResult)
                    } ?: Resource.ErrorOther("Пустой ответ от сервера")
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Resource.ErrorOther("Неизвестная ошибка (код: ${response.resultCode})")
                }
            }
        } catch (e: Exception) {
            Napier.e("AuthRepository.verifySmsCode: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

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

    override suspend fun clearTokens() {
        try {
            tokenStorage.clearTokens()
            _authState.value = false
            Napier.d("Токены успешно очищены")
        } catch (e: Exception) {
            Napier.e("AuthRepository.clearTokens: Exception", e)
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

    companion object {
        private const val POLLING_INTERVAL_FAST_MS = 2000L      // 2 секунды - частое опрашивание
        private const val POLLING_INTERVAL_MEDIUM_MS = 7000L    // 7 секунд - среднее опрашивание
        private const val POLLING_INTERVAL_SLOW_MS = 15000L     // 15 секунд - редкое опрашивание

        private const val FAST_POLLING_START_SECONDS = 60       // Первые 60 секунд - часто
        private const val FAST_POLLING_END_SECONDS = 30         // Последние 30 секунд - часто
        private const val SECONDS_TO_CALL_DEFAULT = 300
    }
}

