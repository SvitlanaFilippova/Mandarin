package com.mandarinkafe.mandarin.core.data.network.auth

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.AuthResponse
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class IikoAuthApi(
    private val client: HttpClient,
) {
    suspend fun authenticate(body: AuthRequest): AuthResponse {
        return try {
            val response = client.post("/api/1/access_token") {
                setBody(body)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<AuthResponse>()
                    authResponse
                }

                else -> {
                    Napier.e("HTTP ${response.status.value}: ${response.status.description}")
                    throw Exception("Ошибка сервера iiko: ${response.status.value}")
                }
            }
        } catch (e: Exception) {
            Napier.e("Ошибка в IikoAuthApi.authenticate", e)
            throw e
        }
    }
}
