package com.mandarinkafe.mandarin.core.data.network

import android.util.Log
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthSyncApi
import com.mandarinkafe.mandarin.util.Constants.HTTP_UNAUTHORIZED
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val authApi: IikoAuthSyncApi,
) : Interceptor {
    @Volatile
    private var token: String? = null
    private val apiKey = BuildConfig.IIKO_API_KEY

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            var request = chain.request()

            // безопасно получаем токен
            val currentToken = token ?: try {
                fetchToken().also { token = it }
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Не удалось получить токен", e)
                null
            }

            if (currentToken != null) {
                request = request.newBuilder()
                    .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + currentToken)
                    .build()
            }

            val response = chain.proceed(request)

            if (response.code == HTTP_UNAUTHORIZED) {
                response.close()
                token = try {
                    fetchToken()
                } catch (e: Exception) {
                    Log.e("AuthInterceptor", "Не удалось обновить токен", e)
                    null
                }

                token?.let {
                    val newRequest = request.newBuilder()
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + it)
                        .build()
                    return chain.proceed(newRequest)
                }
            }

            response
        } catch (e: IOException) {
            Log.e("AuthInterceptor", "Ошибка в intercept", e)
            // Возвращаем фейковый ответ с кодом 500, чтобы не крашить
            chain.proceed(chain.request().newBuilder().build())
        }
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