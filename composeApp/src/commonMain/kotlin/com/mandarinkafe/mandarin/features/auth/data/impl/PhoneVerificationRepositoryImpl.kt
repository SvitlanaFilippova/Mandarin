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
import com.mandarinkafe.mandarin.features.auth.util.getAppSignatureHashForSms
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
        return try {
            val request = PhoneVerificationRequest(phone)
            val response = networkClient.requestPhoneVerification(request)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as? PhoneVerificationResponse
                    val data = wrapper?.data

                    when {
                        wrapper == null -> Resource.ErrorOther(ERROR_CONVERSION)
                        data != null -> Resource.Success(data.toDomain())
                        else -> Resource.ErrorOther(ERROR_EMPTY_RESPONSE)
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther(ERROR_SERVER)
                }

                else -> {
                    Resource.ErrorOther("$ERROR_UNKNOWN_WITH_CODE ${response.resultCode})")
                }
            }
        } catch (e: Exception) {
            Resource.ErrorOther(formatError(e))
        }
    }

    override suspend fun checkVerificationStatusByCheckId(checkId: String): Resource<PhoneVerificationStatus> {
        return try {
            val deviceName = deviceInfoProvider.getDeviceName()
            val response = networkClient.checkVerificationStatusByCheckId(
                PhoneVerificationStatusByCheckIdRequest(checkId, deviceName)
            )

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val wrapper = response as PhoneVerificationStatusResponse
                    wrapper.data?.let {
                        Resource.Success(it.toDomain())
                    } ?: run {
                        Resource.ErrorOther(ERROR_EMPTY_RESPONSE)
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther(ERROR_SERVER)
                }

                else -> {
                    Resource.ErrorOther(ERROR_UNKNOWN)
                }
            }
        } catch (e: Exception) {
            Resource.ErrorOther(formatError(e))
        }
    }

    override fun observeVerificationStatusByPhone(phone: String): Flow<Resource<PhoneVerificationStatus>> =
        flow {
            val deviceName = deviceInfoProvider.getDeviceName()
            val request =
                PhoneVerificationStatusByPhoneRequest(phone = phone, deviceName = deviceName)

            while (true) {
                val delayMs = try {
                    val response = checkVerificationStatusByPhone(request)
                    emit(response)
                    calculateDelayAndCheckIfShouldContinue(response)
                } catch (e: Exception) {
                    Napier.e(
                        "PhoneVerificationRepository: observeVerificationStatusByPhone - Exception",
                        e
                    )
                    emit(Resource.ErrorOther(formatError(e)))
                    POLLING_INTERVAL_SLOW_MS
                }

                if (delayMs == null) break
                delay(delayMs)
            }
        }

    private fun calculateDelayAndCheckIfShouldContinue(response: Resource<PhoneVerificationStatus>): Long? {
        return when (response) {
            is Resource.Success -> handleSuccessResponse(response.data)
            is Resource.ErrorNoInternet -> POLLING_INTERVAL_MEDIUM_MS
            else -> POLLING_INTERVAL_SLOW_MS
        }
    }

    private fun handleSuccessResponse(status: PhoneVerificationStatus?): Long? {
        if (status == null) return POLLING_INTERVAL_SLOW_MS
        if (status.shouldStopPolling == true) return null

        val expiresIn = status.expiresInSeconds
        if (expiresIn == null || expiresIn <= 0) return null

        return calculatePollingInterval(expiresIn)
    }

    private fun calculatePollingInterval(expiresInSeconds: Int): Long {
        return when {
            expiresInSeconds > SECONDS_TO_CALL_DEFAULT - FAST_POLLING_START_SECONDS -> POLLING_INTERVAL_FAST_MS
            expiresInSeconds <= FAST_POLLING_END_SECONDS -> POLLING_INTERVAL_FAST_MS
            else -> POLLING_INTERVAL_MEDIUM_MS
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
                        Resource.Success(it.toDomain())
                    } ?: run {
                        Resource.ErrorOther(ERROR_EMPTY_RESPONSE)
                    }
                }

                HTTP_SERVER_ERROR -> {
                    Resource.ErrorOther(ERROR_SERVER)
                }

                else -> {
                    Resource.ErrorOther(ERROR_UNKNOWN)
                }
            }
        } catch (e: Exception) {
            Resource.ErrorOther(formatError(e))
        }
    }

    override suspend fun requestSmsVerification(phone: String): Resource<SmsVerificationData> {
        return try {
            val platform = deviceInfoProvider.getPlatform()
            // Получаем хэш подписи приложения для SMS Retriever (только для Android)
            val appSignatureHash = getAppSignatureHashForSms()
            if (appSignatureHash != null) {
                Napier.i { "SMS Verification: отправляем хэш подписи приложения: $appSignatureHash" }
            } else if (platform == "android") {
                Napier.w { "SMS Verification: не удалось получить хэш подписи приложения для Android" }
            }
            val response = networkClient.requestSmsVerification(
                SmsVerificationRequest(
                    phone = phone,
                    platform = platform,
                    appSignatureHash = appSignatureHash
                )
            )
            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val wrapper = response as SmsVerificationResponse
                    wrapper.data?.let { dto ->
                        Resource.Success(dto.toDomain())
                    } ?: Resource.ErrorOther(ERROR_EMPTY_RESPONSE)
                }

                HTTP_SERVER_ERROR -> Resource.ErrorOther(ERROR_SERVER)
                else -> Resource.ErrorOther("Неизвестная ошибка (код: ${response.resultCode})")
            }
        } catch (e: Exception) {
            Resource.ErrorOther(formatError(e))
        }
    }

    override suspend fun verifySmsCode(phone: String, code: String): Resource<VerifySmsCodeResult> {
        return try {
            val deviceName = deviceInfoProvider.getDeviceName()
            val response =
                networkClient.verifySmsCode(VerifySmsCodeRequest(phone, code, deviceName))
            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val wrapper = response as VerifySmsCodeResponse
                    wrapper.data?.let { dto ->
                        Resource.Success(dto.toDomain())
                    } ?: Resource.ErrorOther(ERROR_EMPTY_RESPONSE)
                }

                HTTP_SERVER_ERROR -> Resource.ErrorOther(ERROR_SERVER)
                else -> Resource.ErrorOther("Неизвестная ошибка (код: ${response.resultCode})")
            }
        } catch (e: Exception) {
            Resource.ErrorOther(formatError(e))
        }
    }

    private fun formatError(e: Exception): String {
        return "$ERROR ${e.message}"
    }

    companion object {
        private const val POLLING_INTERVAL_FAST_MS = 2000L // 2 секунды - частое опрашивание
        private const val POLLING_INTERVAL_MEDIUM_MS = 7000L // 7 секунд - среднее опрашивание
        private const val POLLING_INTERVAL_SLOW_MS = 15000L // 15 секунд - редкое опрашивание

        private const val FAST_POLLING_START_SECONDS = 60 // Первые 60 секунд - часто
        private const val FAST_POLLING_END_SECONDS = 30 // Последние 30 секунд - часто
        private const val SECONDS_TO_CALL_DEFAULT = 300

        // Error messages
        private const val ERROR_EMPTY_RESPONSE = "Пустой ответ от сервера"
        private const val ERROR = "Ошибка:"
        private const val ERROR_SERVER = "Ошибка сервера"
        private const val ERROR_UNKNOWN = "Неизвестная ошибка"
        private const val ERROR_UNKNOWN_WITH_CODE = "Неизвестная ошибка (код:"
        private const val ERROR_CONVERSION = "Ошибка преобразования ответа"
    }
}

