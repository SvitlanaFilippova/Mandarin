package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.menu.data.dto.MenuIdResponse
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface IikoMenuApi {
    @POST("api/2/menu")
    suspend fun getMenuId(): MenuIdResponse

    @POST("api/2/menu/by_id")
    suspend fun getMenuById(
        @Body body: MenuRequest
    ): MenuResponse
}