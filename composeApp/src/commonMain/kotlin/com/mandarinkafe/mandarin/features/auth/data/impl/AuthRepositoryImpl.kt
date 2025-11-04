package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusResponse
import com.mandarinkafe.mandarin.features.auth.data.network.AuthNetworkClient
import com.mandarinkafe.mandarin.features.auth.data.toDomain
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val networkClient: AuthNetworkClient,
) : AuthRepository {


    override suspend fun requestPhoneVerification(phone: String): Resource<PhoneVerificationData> {
        Napier.d("AUTH DEBUG: AuthRepository.requestPhoneVerification() called with phone: ${phone}")
        return try {
            val response = networkClient.requestPhoneVerification(PhoneVerificationRequest(phone))
            Napier.d("AUTH DEBUG: NetworkClient returned response with resultCode: ${response.resultCode}")
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.d("AUTH DEBUG: Returning ErrorNoInternet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationResponse
                    Napier.d("AUTH DEBUG: HTTP_SUCCESS, data: ${wrapper.data}")
                    wrapper.data?.let {
                        val domainData = it.toDomain()
                        Napier.d("AUTH DEBUG: Converted to domain model: checkId=${domainData.checkId}, callPhone=${domainData.phoneToCall}")
                        Resource.Success(domainData)
                    } ?: run {
                        Napier.d("AUTH DEBUG: Warning: wrapper.data is null, returning ErrorOther")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (HTTP_SERVER_ERROR)")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (unknown error code: ${response.resultCode})")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.d("AUTH DEBUG: Exception in requestPhoneVerification: ${e.message}")
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun checkVerificationStatusByCheckId(checkId: String): Resource<PhoneVerificationStatus> {
        Napier.d("AUTH DEBUG: AuthRepository.checkVerificationStatusByCheckId() called with checkId: ${checkId}")
        return try {
            val response = networkClient.checkVerificationStatusByCheckId(
                PhoneVerificationStatusByCheckIdRequest(checkId)
            )
            Napier.d("AUTH DEBUG: NetworkClient returned response with resultCode: ${response.resultCode}")
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.d("AUTH DEBUG: Returning ErrorNoInternet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationStatusResponse
                    Napier.d("AUTH DEBUG: HTTP_SUCCESS, data: ${wrapper.data}")
                    wrapper.data?.let {
                        val domainStatus = it.toDomain()
                        Napier.d("AUTH DEBUG: Converted to domain model: isVerified=${domainStatus.isVerified}, shouldStopPolling=${domainStatus.shouldStopPolling}, expiresInSeconds=${domainStatus.expiresInSeconds}")
                        Resource.Success(domainStatus)
                    } ?: run {
                        Napier.d("AUTH DEBUG: Warning: wrapper.data is null, returning ErrorOther")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (HTTP_SERVER_ERROR)")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (unknown error code: ${response.resultCode})")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.d("AUTH DEBUG: Exception in checkVerificationStatusByCheckId: ${e.message}")
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override fun observeVerificationStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>> =
        flow {
            Napier.d("AUTH DEBUG: observeVerificationStatusByPhone started for phone: $phone")

            val request = PhoneVerificationStatusByPhoneRequest(phone = phone)

            while (true) {
                try {
                    val response = checkVerificationStatusByPhone(request)
                    emit(response)

                    when (response) {
                        is Resource.Success -> {
                            val status = response.data

                            if (status == null) {
                                Napier.d("AUTH DEBUG: Status data is null, emitting error")
                                emit(Resource.ErrorOther("Пустой ответ от сервера"))
                                delay(POLLING_INTERVAL_SLOW_MS)
                            } else {
                                // Останавливаем пулинг, если нужно
                                if (status.shouldStopPolling == true) {
                                    Napier.d("AUTH DEBUG: Should stop polling, ending flow")
                                    break
                                }

                                // Определяем интервал пулинга на основе оставшегося времени
                                val expiresIn = status.expiresInSeconds
                                if (expiresIn == null || expiresIn <= 0) {
                                    Napier.d("AUTH DEBUG: Time expired, stopping polling")
                                    break
                                }

                                val pollingInterval = when {
                                    // Первые 60 секунд - часто
                                    expiresIn > SECONDS_TO_CALL_DEFAULT - FAST_POLLING_START_SECONDS -> {
                                        Napier.d("AUTH DEBUG: Using fast polling (start period)")
                                        POLLING_INTERVAL_FAST_MS
                                    }
                                    // Последние 30 секунд - часто
                                    expiresIn <= FAST_POLLING_END_SECONDS -> {
                                        Napier.d("AUTH DEBUG: Using fast polling (end period)")
                                        POLLING_INTERVAL_FAST_MS
                                    }
                                    // Средний период - реже
                                    else -> {
                                        Napier.d("AUTH DEBUG: Using medium polling (middle period)")
                                        POLLING_INTERVAL_MEDIUM_MS
                                    }
                                }

                                delay(pollingInterval)
                            }
                        }

                        is Resource.ErrorNoInternet -> {
                            Napier.d("AUTH DEBUG: No internet, continuing polling with medium interval")
                            delay(POLLING_INTERVAL_MEDIUM_MS)
                        }

                        else -> {
                            Napier.d("AUTH DEBUG: Error response, continuing polling with slow interval")
                            delay(POLLING_INTERVAL_SLOW_MS)
                        }
                    }
                } catch (e: Exception) {
                    Napier.d("AUTH DEBUG: Exception in observeVerificationStatusByPhone: ${e.message}")
                    emit(Resource.ErrorOther("Ошибка: ${e.message}"))
                    delay(POLLING_INTERVAL_SLOW_MS)
                }
            }

            Napier.d("AUTH DEBUG: observeVerificationStatusByPhone ended")
        }

    private suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Resource<PhoneVerificationStatus> {
        Napier.d("AUTH DEBUG: AuthRepository.checkVerificationStatusByPhone() called with phone: ${request.phone}")
        return try {
            val response = networkClient.checkVerificationStatusByPhone(request)
            Napier.d("AUTH DEBUG: NetworkClient returned response with resultCode: ${response.resultCode}")
            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.d("AUTH DEBUG: Returning ErrorNoInternet")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationStatusResponse
                    Napier.d("AUTH DEBUG: HTTP_SUCCESS, data: ${wrapper.data}")
                    wrapper.data?.let {
                        val domainStatus = it.toDomain()
                        Napier.d("AUTH DEBUG: Converted to domain model: isVerified=${domainStatus.isVerified}, shouldStopPolling=${domainStatus.shouldStopPolling}, expiresInSeconds=${domainStatus.expiresInSeconds}")
                        Resource.Success(domainStatus)
                    } ?: run {
                        Napier.d("AUTH DEBUG: Warning: wrapper.data is null, returning ErrorOther")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (HTTP_SERVER_ERROR)")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.d("AUTH DEBUG: Returning ErrorOther (unknown error code: ${response.resultCode})")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.d("AUTH DEBUG: Exception in checkVerificationStatusByCheckId: ${e.message}")
            Resource.ErrorOther("Ошибка: ${e.message}")
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

