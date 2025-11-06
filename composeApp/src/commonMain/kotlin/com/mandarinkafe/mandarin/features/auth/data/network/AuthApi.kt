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
        return try {
            val httpResponse = client.post("/auth/request") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationDataDto = httpResponse.body()
                    PhoneVerificationResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: requestPhoneVerification(): ошибка запроса верификации", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        return try {
            val httpResponse = client.post("/auth/verify-status") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: checkVerificationStatus(): ошибка проверки статуса", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        return try {
            val httpResponse = client.post("/auth/status") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: checkVerificationStatusByCheckId(): ошибка проверки статуса", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun requestSmsVerification(request: SmsVerificationRequest): Response {
        return try {
            val httpResponse = client.post("/auth/request_sms") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: SmsVerificationDataDto = httpResponse.body()
                    SmsVerificationResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("ServerApi: requestSmsVerification() HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: requestSmsVerification(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response {
        return try {
            val httpResponse = client.post("/auth/verify_sms") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: VerifySmsCodeDataDto = httpResponse.body()
                    VerifySmsCodeResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("ServerApi: verifySmsCode() HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("ServerApi: verifySmsCode(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}