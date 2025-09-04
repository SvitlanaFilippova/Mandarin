package com.mandarinkafe.mandarin.features.more.data.network

import com.mandarinkafe.mandarin.features.more.data.dto.TelegramResponse
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface TelegramApi {
    @POST
    suspend fun sendMessage(
        @Url url: String,
        @Query("chat_id") chatId: String,
        @Query("text") text: String,
        @Query("parse_mode") parseMode: String = "HTML"
    ): TelegramResponse
}