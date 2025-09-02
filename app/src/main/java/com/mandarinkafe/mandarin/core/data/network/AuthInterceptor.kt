package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthSyncApi
import com.mandarinkafe.mandarin.util.Constants.HTTP_UNAUTHORIZED
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authApi: IikoAuthSyncApi,
    private val apiKey: String
) : Interceptor {
    @Volatile
    private var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val currentToken = token ?: fetchToken().also { token = it }

        request = request.newBuilder()
            .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + currentToken)
            .build()

        val response = chain.proceed(request)

        if (response.code == HTTP_UNAUTHORIZED) {
            response.close()
            token = fetchToken()
            val newRequest = request.newBuilder()
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                .build()
            return chain.proceed(newRequest)
        }

        return response
    }

    private fun fetchToken(): String {
        val resp = authApi.authenticate(AuthRequest(apiKey)).execute()
        if (!resp.isSuccessful) error("Ошибка авторизации: ${resp.code()}")
        return resp.body()?.token ?: error("Пустой токен")
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}