package com.mandarinkafe.mandarin.core.data.network.auth

import android.util.Log
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class IikoAuthProvider(
    private val authApi: IikoAuthApi
) : AuthProvider {
    private val mutex = Mutex()
    private var cachedToken: String? = null
    private val apiKey = BuildConfig.IIKO_API_KEY
    private val logTag = "IikoAuthProvider"

    override suspend fun getToken(): String {
        return mutex.withLock {
            if (cachedToken == null) {
                cachedToken = refreshToken()
            }
            cachedToken!!
        }
    }

    override suspend fun refreshToken(): String {
        return mutex.withLock {
            try {
                val response = authApi.authenticate(AuthRequest(apiKey))
                cachedToken = response.token
                    ?: error("Пустой токен от iiko")
                cachedToken!!
            } catch (e: Exception) {
                Log.e(logTag, "Ошибка получения токена", e)
                throw e
            }
        }
    }
}
