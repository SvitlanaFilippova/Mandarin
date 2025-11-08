package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.auth.data.dto.ActiveSessionsDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.ActiveSessionsResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusDto
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.UpdateNameRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.UserInfoDto
import com.mandarinkafe.mandarin.features.auth.data.dto.ValidateTokenResponse
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeDataDto
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class AuthApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY


    private fun String.toAuthorizationHeader() = "$BEARER_TOKEN_TYPE $this"

    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response {
        return try {
            val httpResponse = client.post("/auth/request") {
                header(HEADER_API_KEY, key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationDataDto = httpResponse.body()
                    PhoneVerificationResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: requestPhoneVerification - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: requestPhoneVerification - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        return try {
            val httpResponse = client.post("/auth/verify-status") {
                header(HEADER_API_KEY, key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Response().apply {
                        resultCode = HTTP_SERVER_ERROR
                    }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: checkVerificationStatusByPhone - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        return try {
            val httpResponse = client.post("/auth/status") {
                header(HEADER_API_KEY, key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: checkVerificationStatusByCheckId - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun requestSmsVerification(request: SmsVerificationRequest): Response {
        return try {
            val httpResponse = client.post("/auth/request_sms") {
                header(HEADER_API_KEY, key)
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
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: requestSmsVerification - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: requestSmsVerification - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response {
        return try {
            val httpResponse = client.post("/auth/verify_sms") {
                header(HEADER_API_KEY, key)
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
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: verifySmsCode - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: verifySmsCode - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun validateToken(accessToken: String): Response {
        return try {
            val httpResponse = client.get("/auth/me") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, accessToken.toAuthorizationHeader())
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: UserInfoDto = httpResponse.body()
                    ValidateTokenResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("$LOG_TAG: validateToken - $LOG_HTTP_ERROR ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: validateToken - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun refreshToken(request: RefreshTokenRequest): Response {
        return try {
            val httpResponse = client.post("/auth/refresh_token") {
                header(HEADER_API_KEY, key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: RefreshTokenDataDto = httpResponse.body()
                    RefreshTokenResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: refreshToken - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: refreshToken - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getActiveSessions(accessToken: String): Response {
        return try {
            val httpResponse = client.get("/auth/active_sessions") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, accessToken.toAuthorizationHeader())
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: ActiveSessionsDataDto = httpResponse.body()
                    ActiveSessionsResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: getActiveSessions - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: getActiveSessions - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun revokeSession(accessToken: String, request: RevokeSessionRequest): Response {
        return try {
            val httpResponse = client.post("/auth/revoke_session") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, accessToken.toAuthorizationHeader())
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: RevokeSessionDataDto = httpResponse.body()
                    RevokeSessionResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: revokeSession - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: revokeSession - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun logout(accessToken: String): Response {
        return try {
            val httpResponse = client.post("/auth/logout") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, accessToken.toAuthorizationHeader())
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: logout - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: logout - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun updateUserName(accessToken: String, request: UpdateNameRequest): Response {
        return try {
            val httpResponse = client.patch("/auth/me/name") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, accessToken.toAuthorizationHeader())
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG: updateUserName - $LOG_HTTP_ERROR ${httpResponse.status.value}: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG: updateUserName - $LOG_EXCEPTION", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private companion object {
        const val LOG_TAG = "AuthApi"
        const val HEADER_API_KEY = "x-api-key"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val ERROR_BODY_READ_FAILED = "Failed to read error body: "
        const val LOG_HTTP_ERROR = "HTTP error"
        const val LOG_EXCEPTION = "Exception"
    }
}