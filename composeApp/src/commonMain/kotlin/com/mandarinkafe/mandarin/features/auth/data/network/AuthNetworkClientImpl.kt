package com.mandarinkafe.mandarin.features.auth.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByCheckIdRequest
import com.mandarinkafe.mandarin.features.auth.data.dto.PhoneVerificationStatusByPhoneRequest
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
        Napier.d("AUTH DEBUG: ServerNetworkClient.requestPhoneVerification() called with phone: ${request.phone}")
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.requestPhoneVerification(request)
    }

    override suspend fun checkVerificationStatusByPhone(request: PhoneVerificationStatusByPhoneRequest): Response {
        Napier.d("AUTH DEBUG: ServerNetworkClient.checkVerificationStatus() called with phone: ${request.phone}")
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.checkVerificationStatusByPhone(request)
    }

    override suspend fun checkVerificationStatusByCheckId(request: PhoneVerificationStatusByCheckIdRequest): Response {
        Napier.d("AUTH DEBUG: ServerNetworkClient.checkVerificationStatusByCheckId() called with checkId: ${request.checkId}")
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.checkVerificationStatusByCheckId(request)
    }

    override suspend fun requestSmsVerification(request: SmsVerificationRequest): Response {
        Napier.d("SMS AUTH DEBUG: ServerNetworkClient.requestSmsVerification() called with phone: ${request.phone}")
        if (!isConnected()) {
            Napier.d("SMS AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.requestSmsVerification(request)
    }

    override suspend fun verifySmsCode(request: VerifySmsCodeRequest): Response {
        Napier.d("SMS AUTH DEBUG: ServerNetworkClient.verifySmsCode() called with phone: ${request.phone}, code: ${request.code}")
        if (!isConnected()) {
            Napier.d("SMS AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.verifySmsCode(request)
    }

    override suspend fun validateToken(accessToken: String): Response {
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.validateToken(accessToken)
    }

    override suspend fun refreshToken(refreshToken: String): Response {
        if (!isConnected()) {
            Napier.d("AUTH DEBUG: No network connection, returning NO_CONNECTION")
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return api.refreshToken(
            com.mandarinkafe.mandarin.features.auth.data.dto.RefreshTokenRequest(refreshToken)
        )
    }
}