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

class AuthNetworkClientImpl(
    private val api: AuthApi,
    private val networkMonitor: NetworkMonitor,
) : AuthNetworkClient {

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun requestPhoneVerification(request: PhoneVerificationRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.requestPhoneVerification(request)
    }

    override suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.checkVerificationStatusByPhone(request)
    }

    override suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.checkVerificationStatusByCheckId(request)
    }

    override suspend fun requestSmsVerification(request: SmsVerificationRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.requestSmsVerification(request)
    }

    override suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.verifySmsCode(request)
    }

    override suspend fun validateToken(accessToken: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.validateToken(accessToken)
    }

    override suspend fun refreshToken(refreshToken: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.refreshToken(
            com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenRequest(refreshToken)
        )
    }

    override suspend fun getActiveSessions(accessToken: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.getActiveSessions(accessToken)
    }

    override suspend fun revokeSession(
        accessToken: String,
        request: RevokeSessionRequest,
    ): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.revokeSession(accessToken, request)
    }

    override suspend fun logout(accessToken: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.logout(accessToken)
    }
}
