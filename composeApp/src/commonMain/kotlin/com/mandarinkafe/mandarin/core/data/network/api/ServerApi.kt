package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.features.menu.data.dto.ServerMenuResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class ServerApi(
    private val client: HttpClient
) {
    private val key = BuildKonfig.MANDARIN_API_KEY
    
    suspend fun getMenu(): ServerMenuResponse {
        return client.get("/menu") {
            header("x-api-key", key)
        }.body()
    }
}
