package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface IikoAuthSyncApi {
    @POST("/api/1/access_token")
    fun authenticate(@Body request: AuthRequest): retrofit2.Call<AuthResponse>
}