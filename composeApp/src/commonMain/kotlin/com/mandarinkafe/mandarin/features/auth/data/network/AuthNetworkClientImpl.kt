package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.RevokeSessionRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.SmsVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.VerifySmsCodeRequest
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import io.github.aakira.napier.Napier

class AuthNetworkClientImpl(
    private val api: AuthApi,
    private val networkMonitor: NetworkMonitor,
) : AuthNetworkClient {

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestPhoneVerification() START - phone: ${request.phone}")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestPhoneVerification() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: requestPhoneVerification() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestPhoneVerification() - Calling api.requestPhoneVerification()")
        val response = api.requestPhoneVerification(request)
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestPhoneVerification() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByPhone() START - phone: ${request.phone}")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByPhone() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByPhone() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.checkVerificationStatusByPhone(request)
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByPhone() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByCheckId() START - checkId: ${request.checkId}")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByCheckId() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByCheckId() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.checkVerificationStatusByCheckId(request)
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: checkVerificationStatusByCheckId() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun requestSmsVerification(request: SmsVerificationRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestSmsVerification() START - phone: ${request.phone}")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestSmsVerification() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: requestSmsVerification() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.requestSmsVerification(request)
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: requestSmsVerification() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: verifySmsCode() START - phone: ${request.phone}, code: ${request.code}")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: verifySmsCode() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: verifySmsCode() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.verifySmsCode(request)
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: verifySmsCode() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun validateToken(accessToken: String): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: validateToken() START")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: validateToken() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: validateToken() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.validateToken(accessToken)
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: validateToken() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun refreshToken(refreshToken: String): Response {
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: refreshToken() START")

        val isNetworkAvailable = isConnected()
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: refreshToken() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("Auth CALL DEBUG: AuthNetworkClient: refreshToken() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.refreshToken(
            com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenRequest(refreshToken)
        )
        Napier.d("Auth CALL DEBUG: AuthNetworkClient: refreshToken() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun getActiveSessions(accessToken: String): Response {
        Napier.d("AuthNetworkClient: getActiveSessions() START")

        val isNetworkAvailable = isConnected()
        Napier.d("AuthNetworkClient: getActiveSessions() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("AuthNetworkClient: getActiveSessions() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.getActiveSessions(accessToken)
        Napier.d("AuthNetworkClient: getActiveSessions() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun revokeSession(
        accessToken: String,
        request: RevokeSessionRequest,
    ): Response {
        Napier.d("AuthNetworkClient: revokeSession() START - sessionId: ${request.tokenId}")

        val isNetworkAvailable = isConnected()
        Napier.d("AuthNetworkClient: revokeSession() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("AuthNetworkClient: revokeSession() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.revokeSession(accessToken, request)
        Napier.d("AuthNetworkClient: revokeSession() - Response received, resultCode: ${response.resultCode}")

        return response
    }

    override suspend fun logout(accessToken: String): Response {
        Napier.d("AuthNetworkClient: logout() START")

        val isNetworkAvailable = isConnected()
        Napier.d("AuthNetworkClient: logout() - Network available: $isNetworkAvailable")

        if (!isNetworkAvailable) {
            Napier.w("AuthNetworkClient: logout() - No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }

        val response = api.logout(accessToken)
        Napier.d("AuthNetworkClient: logout() - Response received, resultCode: ${response.resultCode}")

        return response
    }
}