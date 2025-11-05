package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class AuthApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response {
        Napier.d("AUTH DEBUG: Sending POST /auth/request with phone: ${request.phone}")
        return try {
            val httpResponse = client.post("/auth/request") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationDataDto = httpResponse.body()
                    Napier.d("AUTH DEBUG: Received response from /auth/request, checkId: ${data.checkId}, phoneToCall: ${data.phoneToCall}")
                    PhoneVerificationResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    Napier.d("AUTH DEBUG: HTTP error status: ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: requestPhoneVerification(): ошибка запроса верификации", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        Napier.d("AUTH DEBUG: Sending POST /auth/verify-status with phone: ${request.phone}")
        return try {
            val httpResponse = client.post("/auth/verify-status") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    Napier.d("AUTH DEBUG: Received response from /auth/verify-status, isVerified: ${data.isVerified}, shouldStopPolling: ${data.shouldStopPolling}, expiresInSeconds: ${data.expiresInSeconds}")
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    Napier.d("AUTH DEBUG: HTTP error status: ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: checkVerificationStatus(): ошибка проверки статуса", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        Napier.d("AUTH DEBUG: Sending POST /auth/status with checkId: ${request.checkId}")
        return try {
            val httpResponse = client.post("/auth/status") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    Napier.d("AUTH DEBUG: Received response from /auth/status, checkId: ${data.checkId}, status: ${data.status}")
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    Napier.d("AUTH DEBUG: HTTP error status: ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: checkVerificationStatusByCheckId(): ошибка проверки статуса", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun requestSmsVerification(request: SmsVerificationRequest): Response {
        Napier.d("SMS AUTH DEBUG: Sending POST /auth/request_sms with phone: ${request.phone}")
        return try {
            val httpResponse = client.post("/auth/request_sms") {
                header("x-api-key", key)
                setBody(request)
            }

            Napier.d("SMS AUTH DEBUG: Response status code: ${httpResponse.status.value} (${httpResponse.status.description})")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: SmsVerificationDataDto = httpResponse.body()
                    Napier.d("SMS AUTH DEBUG: Received response from /auth/request_sms, status: ${data.status}, expiresIn: ${data.expiresIn}")
                    SmsVerificationResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("SMS AUTH DEBUG: HTTP error status: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: requestSmsVerification(): Exception occurred", e)
            Napier.e("SMS AUTH DEBUG: Exception type: ${e::class.simpleName}, message: ${e.message}")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response {
        Napier.d("SMS AUTH DEBUG: Sending POST /auth/verify_sms with phone: ${request.phone}, code: ${request.code}")
        return try {
            val httpResponse = client.post("/auth/verify_sms") {
                header("x-api-key", key)
                setBody(request)
            }

            Napier.d("SMS AUTH DEBUG: Response status code: ${httpResponse.status.value} (${httpResponse.status.description})")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: VerifySmsCodeDataDto = httpResponse.body()
                    Napier.d("SMS AUTH DEBUG: Received response from /auth/verify_sms, isVerified: ${data.isVerified}")
                    VerifySmsCodeResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("SMS AUTH DEBUG: HTTP error status: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: verifySmsCode(): Exception occurred", e)
            Napier.e("SMS AUTH DEBUG: Exception type: ${e::class.simpleName}, message: ${e.message}")
            e.printStackTrace()
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}