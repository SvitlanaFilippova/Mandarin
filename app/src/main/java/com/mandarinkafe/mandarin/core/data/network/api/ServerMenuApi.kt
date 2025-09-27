package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.menu.data.dto.ServerMenuResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface ServerMenuApi {
    @GET("menu")
    suspend fun getMenu(
        @Header("x-api-key") apiKey: String
    ): ServerMenuResponse
}
