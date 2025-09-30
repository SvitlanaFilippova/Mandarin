package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody


class IikoAuthApi(
    private val client: HttpClient
) {
    suspend fun authenticate(body: AuthRequest): AuthResponse {
        return client.post("/api/1/access_token") {
            setBody(body)
        }.body()
    }
}