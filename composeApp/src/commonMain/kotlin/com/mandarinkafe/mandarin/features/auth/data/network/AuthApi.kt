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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class AuthApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthApi: requestPhoneVerification() START - phone: ${request.phone}")
        return try {
            Napier.d("Auth CALL DEBUG: AuthApi: requestPhoneVerification() - Отправка POST запроса на /auth/request")
            val httpResponse = client.post("/auth/request") {
                header("x-api-key", key)
                setBody(request)
            }

            Napier.d("Auth CALL DEBUG: AuthApi: requestPhoneVerification() - Получен ответ, status: ${httpResponse.status.value}")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationDataDto = httpResponse.body()
                    Napier.d("Auth CALL DEBUG: AuthApi: requestPhoneVerification() - SUCCESS, данные: checkId=${data.checkId}, expiresInSeconds=${data.expiresInSeconds}")
                    PhoneVerificationResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("Auth CALL DEBUG: AuthApi: requestPhoneVerification() - HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e(
                "Auth CALL DEBUG: AuthApi: requestPhoneVerification(): EXCEPTION - ${e.message}",
                e
            )
            Napier.e("Auth CALL DEBUG: AuthApi: requestPhoneVerification(): Exception type: ${e::class.simpleName}")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthApi: checkVerificationStatusByPhone() START - phone: ${request.phone}")
        return try {
            val httpResponse = client.post("/auth/verify-status") {
                header("x-api-key", key)
                setBody(request)
            }

            Napier.d("Auth CALL DEBUG: AuthApi: checkVerificationStatusByPhone() - Получен ответ, status: ${httpResponse.status.value}")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    Napier.d("Auth CALL DEBUG: AuthApi: checkVerificationStatusByPhone() - SUCCESS, status: ${data.status}")
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("Auth CALL DEBUG: AuthApi: checkVerificationStatusByPhone() - HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e(
                "Auth CALL DEBUG: AuthApi: checkVerificationStatusByPhone(): EXCEPTION - ${e.message}",
                e
            )
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthApi: checkVerificationStatusByCheckId() START - checkId: ${request.checkId}")
        return try {
            val httpResponse = client.post("/auth/status") {
                header("x-api-key", key)
                setBody(request)
            }

            Napier.d("Auth CALL DEBUG: AuthApi: checkVerificationStatusByCheckId() - Получен ответ, status: ${httpResponse.status.value}")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: PhoneVerificationStatusDto = httpResponse.body()
                    Napier.d("Auth CALL DEBUG: AuthApi: checkVerificationStatusByCheckId() - SUCCESS, status: ${data.status}")
                    PhoneVerificationStatusResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("Auth CALL DEBUG: AuthApi: checkVerificationStatusByCheckId() - HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e(
                "Auth CALL DEBUG: AuthApi: checkVerificationStatusByCheckId(): EXCEPTION - ${e.message}",
                e
            )
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

    suspend fun validateToken(accessToken: String): Response {
        return try {
            val httpResponse = client.get("/auth/me") {
                header("x-api-key", key)
                header("Authorization", "$BEARER_TOKEN_TYPE $accessToken")
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: UserInfoDto = httpResponse.body()
                    ValidateTokenResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.d("AuthApi: validateToken() - Token is invalid or expired (401)")
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("AuthApi: validateToken() HTTP error: ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AuthApi: validateToken(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun refreshToken(request: RefreshTokenRequest): Response {
        return try {
            val httpResponse = client.post("/auth/refresh_token") {
                header("x-api-key", key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: RefreshTokenDataDto = httpResponse.body()
                    RefreshTokenResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.d("AuthApi: refreshToken() - Refresh token is invalid or expired (401)")
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("AuthApi: refreshToken() HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AuthApi: refreshToken(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getActiveSessions(accessToken: String): Response {
        return try {
            Napier.d("AuthApi: getActiveSessions() START")
            val httpResponse = client.get("/auth/active_sessions") {
                header("x-api-key", key)
                header("Authorization", "$BEARER_TOKEN_TYPE $accessToken")
            }

            Napier.d("AuthApi: getActiveSessions() - Response received, status: ${httpResponse.status.value}")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: ActiveSessionsDataDto = httpResponse.body()
                    Napier.d("AuthApi: getActiveSessions() - SUCCESS, sessions count: ${data.sessions.size}")
                    ActiveSessionsResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.d("AuthApi: getActiveSessions() - Unauthorized (401)")
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("AuthApi: getActiveSessions() HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AuthApi: getActiveSessions(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun revokeSession(accessToken: String, request: RevokeSessionRequest): Response {
        return try {
            Napier.d("AuthApi: revokeSession() START - sessionId: ${request.tokenId}")
            val httpResponse = client.post("/auth/revoke_session") {
                header("x-api-key", key)
                header("Authorization", "$BEARER_TOKEN_TYPE $accessToken")
                setBody(request)
            }

            Napier.d("AuthApi: revokeSession() - Response received, status: ${httpResponse.status.value}")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data: RevokeSessionDataDto = httpResponse.body()
                    Napier.d("AuthApi: revokeSession() - SUCCESS")
                    RevokeSessionResponse(data = data).apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.d("AuthApi: revokeSession() - Unauthorized (401)")
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("AuthApi: revokeSession() HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AuthApi: revokeSession(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun logout(accessToken: String): Response {
        return try {
            Napier.d("AuthApi: logout() START")
            val httpResponse = client.post("/auth/logout") {
                header("x-api-key", key)
                header("Authorization", "$BEARER_TOKEN_TYPE $accessToken")
            }

            Napier.d("AuthApi: logout() - Response received, status: ${httpResponse.status.value}")

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Napier.d("AuthApi: logout() - SUCCESS")
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.d("AuthApi: logout() - Unauthorized (401)")
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.body<String>()
                    } catch (e: Exception) {
                        "Failed to read error body: ${e.message}"
                    }
                    Napier.e("AuthApi: logout() HTTP error: ${httpResponse.status.value}, body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AuthApi: logout(): Exception occurred", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}