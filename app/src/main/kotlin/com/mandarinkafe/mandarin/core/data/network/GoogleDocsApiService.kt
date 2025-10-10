package com.mandarinkafe.mandarin.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GoogleDocsApiService(
    private val client: HttpClient
) {
    suspend fun getCsv(url: String): String {
        return client.get(url) {
        }.body()
    }
}