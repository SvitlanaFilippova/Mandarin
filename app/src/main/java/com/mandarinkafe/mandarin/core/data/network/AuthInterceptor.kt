package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.network.auth.AuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header

class AuthInterceptor(
    private val authProvider: AuthProvider
) {
    suspend fun intercept(request: HttpRequestBuilder) {
        val token = authProvider.getToken()
        request.header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
