package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.menu.data.dto.ServerMenuResponse
import retrofit2.http.GET

interface ServerMenuApi {
    @GET("menu")
    suspend fun getMenu(): ServerMenuResponse
}