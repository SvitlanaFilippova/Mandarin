package com.mandarinkafe.mandarin.features.more.data.network

import com.mandarinkafe.mandarin.features.more.data.dto.TelegramResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class TelegramApi(
    private val client: HttpClient,
) {
    suspend fun sendMessage(
        url: String,
        chatId: String,
        text: String,
        parseMode: String = "HTML",
    ): TelegramResponse {
        return client.post(url) {
            parameter("chat_id", chatId)
            parameter("text", text)
            parameter("parse_mode", parseMode)
        }.body()
    }
}
