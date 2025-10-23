package com.mandarinkafe.mandarin.core.data.network.auth

import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import io.github.aakira.napier.Napier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class IikoAuthProvider(
    private val authApi: IikoAuthApi
) {
    private var token: String? = null
    private val mutex = Mutex()
    private val apiKey = BuildKonfig.IIKO_API_KEY

    suspend fun getToken(): String {
        return mutex.withLock {
            val current = token
            if (current != null) {
                return current
            }
            val newToken = fetchNewToken()
            token = newToken
            newToken
        }
    }

    suspend fun refreshToken(): String {
        return mutex.withLock {
            fetchNewToken().also { token = it }
        }
    }

    private suspend fun fetchNewToken(): String {
        return try {
            val response = authApi.authenticate(AuthRequest(apiKey))
            token = response.token
                ?: error("Пустой токен от iiko")
            token!!
        } catch (e: Exception) {
            Napier.e("Ошибка получения токена", e)
            throw e
        }
    }
}
