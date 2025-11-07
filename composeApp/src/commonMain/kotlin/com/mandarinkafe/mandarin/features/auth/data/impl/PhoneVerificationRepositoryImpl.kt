package com.mandarinkafe.mandarin.features.auth.data.impl

import com.mandarinkafe.mandarin.features.auth.data.Mapper.toDomain
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
import com.mandarinkafe.mandarin.features.auth.domain.api.PhoneVerificationRepository
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.PhoneVerificationStatus
import com.mandarinkafe.mandarin.features.auth.domain.models.SmsVerificationData
import com.mandarinkafe.mandarin.features.auth.domain.models.VerifySmsCodeResult
import com.mandarinkafe.mandarin.shared.device.DeviceInfoProvider
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PhoneVerificationRepositoryImpl(
    private val networkClient: AuthNetworkClient,
    private val deviceInfoProvider: DeviceInfoProvider,
) : PhoneVerificationRepository {

    override suspend fun requestPhoneVerification(phone: String): Resource<PhoneVerificationData> {
        Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() START - phone: $phone")
        return try {
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Creating request")
            val request = PhoneVerificationRequest(phone)
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Calling networkClient")

            val response = networkClient.requestPhoneVerification(request)
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Response received, resultCode: ${response.resultCode}")

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.w("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - NO_CONNECTION")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - HTTP_SUCCESS, parsing response")
                    val wrapper = response as? PhoneVerificationResponse

                    if (wrapper == null) {
                        Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Failed to cast response to PhoneVerificationResponse, actual type: ${response::class.simpleName}")
                        Resource.ErrorOther("Ошибка преобразования ответа")
                    } else {
                        wrapper.data?.let {
                            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Data received: checkId=${it.checkId}")
                            val domainData = it.toDomain()
                            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - SUCCESS, returning data")
                            Resource.Success(domainData)
                        } ?: run {
                            Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Response data is NULL")
                            Resource.ErrorOther("Пустой ответ от сервера")
                        }
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - HTTP_SERVER_ERROR")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка (код: ${response.resultCode})")
                }
            }
        } catch (e: Exception) {
            Napier.e(
                "Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - EXCEPTION: ${e.message}",
                e
            )
            Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Exception type: ${e::class.simpleName}")
            Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: requestPhoneVerification() - Stack trace: ${e.stackTraceToString()}")
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun checkVerificationStatusByCheckId(checkId: String): Resource<PhoneVerificationStatus> {
        Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() START - checkId: $checkId")
        return try {
            val deviceName = deviceInfoProvider.getDeviceName()
            val response = networkClient.checkVerificationStatusByCheckId(
                PhoneVerificationStatusByCheckIdRequest(checkId, deviceName)
            )
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - Response received, resultCode: ${response.resultCode}")

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.w("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - NO_CONNECTION")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - HTTP_SUCCESS, parsing response")
                    val wrapper = response as PhoneVerificationStatusResponse
                    wrapper.data?.let {
                        Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - Status: ${it.status}, shouldStopPolling: ${it.shouldStopPolling}")
                        val domainStatus = it.toDomain()
                        Resource.Success(domainStatus)
                    } ?: run {
                        Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - Response data is NULL")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - HTTP_SERVER_ERROR")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e(
                "Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByCheckId() - EXCEPTION: ${e.message}",
                e
            )
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override fun observeVerificationStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>> =
        flow {
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() START - phone: $phone")
            val deviceName = deviceInfoProvider.getDeviceName()
            val request =
                PhoneVerificationStatusByPhoneRequest(phone = phone, deviceName = deviceName)

            while (true) {
                try {
                    val response = checkVerificationStatusByPhone(request)
                    emit(response)

                    when (response) {
                        is Resource.Success -> {
                            val status = response.data

                            if (status == null) {
                                Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - Status data is NULL")
                                emit(Resource.ErrorOther("Пустой ответ от сервера"))
                                delay(POLLING_INTERVAL_SLOW_MS)
                            } else {
                                Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - Status received: ${status.status}, shouldStopPolling: ${status.shouldStopPolling}")

                                // Останавливаем пулинг, если нужно
                                if (status.shouldStopPolling == true) {
                                    Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - Stopping polling (shouldStopPolling=true)")
                                    break
                                }

                                // Определяем интервал пулинга на основе оставшегося времени
                                val expiresIn = status.expiresInSeconds
                                if (expiresIn == null || expiresIn <= 0) {
                                    Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - Stopping polling (expired)")
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

                                Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - Next poll in ${pollingInterval}ms, expiresIn: ${expiresIn}s")
                                delay(pollingInterval)
                            }
                        }

                        is Resource.ErrorNoInternet -> {
                            Napier.w("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - No internet, retrying in ${POLLING_INTERVAL_MEDIUM_MS}ms")
                            delay(POLLING_INTERVAL_MEDIUM_MS)
                        }

                        else -> {
                            Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - Error response, retrying in ${POLLING_INTERVAL_SLOW_MS}ms")
                            delay(POLLING_INTERVAL_SLOW_MS)
                        }
                    }
                } catch (e: Exception) {
                    Napier.e(
                        "Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - EXCEPTION: ${e.message}",
                        e
                    )
                    emit(Resource.ErrorOther("Ошибка: ${e.message}"))
                    delay(POLLING_INTERVAL_SLOW_MS)
                }
            }
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: observeVerificationStatusByPhone() - END")
        }

    private suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Resource<PhoneVerificationStatus> {
        Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() START - phone: ${request.phone}")
        return try {
            val response = networkClient.checkVerificationStatusByPhone(request)
            Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - Response received, resultCode: ${response.resultCode}")

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Napier.w("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - NO_CONNECTION")
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - HTTP_SUCCESS, parsing response")
                    val wrapper = response as PhoneVerificationStatusResponse
                    wrapper.data?.let {
                        Napier.d("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - Status: ${it.status}, shouldStopPolling: ${it.shouldStopPolling}, expiresInSeconds: ${it.expiresInSeconds}")
                        val domainStatus = it.toDomain()
                        Resource.Success(domainStatus)
                    } ?: run {
                        Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - Response data is NULL")
                        Resource.ErrorOther("Пустой ответ от сервера")
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - HTTP_SERVER_ERROR")
                    Resource.ErrorOther("Ошибка сервера")
                }

                else -> {
                    Napier.e("Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - Unknown error code: ${response.resultCode}")
                    Resource.ErrorOther("Неизвестная ошибка")
                }
            }
        } catch (e: Exception) {
            Napier.e(
                "Auth CALL DEBUG: PhoneVerificationRepository: checkVerificationStatusByPhone() - EXCEPTION: ${e.message}",
                e
            )
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
            Napier.e("PhoneVerificationRepository.requestSmsVerification: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun verifySmsCode(phone: String, code: String): Resource<VerifySmsCodeResult> {
        return try {
            val deviceName = deviceInfoProvider.getDeviceName()
            val response =
                networkClient.verifySmsCode(VerifySmsCodeRequest(phone, code, deviceName))
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
            Napier.e("PhoneVerificationRepository.verifySmsCode: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    companion object {
        private const val POLLING_INTERVAL_FAST_MS = 2000L  // 2 секунды - частое опрашивание
        private const val POLLING_INTERVAL_MEDIUM_MS = 7000L // 7 секунд - среднее опрашивание
        private const val POLLING_INTERVAL_SLOW_MS = 15000L // 15 секунд - редкое опрашивание

        private const val FAST_POLLING_START_SECONDS = 60 // Первые 60 секунд - часто
        private const val FAST_POLLING_END_SECONDS = 30 // Последние 30 секунд - часто
        private const val SECONDS_TO_CALL_DEFAULT = 300
    }
}

