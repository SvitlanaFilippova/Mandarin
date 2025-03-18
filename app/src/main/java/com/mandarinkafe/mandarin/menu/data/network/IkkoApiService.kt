package com.mandarinkafe.mandarin.menu.data.network
import com.mandarinkafe.mandarin.menu.data.dto.AuthRequest
import com.mandarinkafe.mandarin.menu.data.dto.AuthResponse
import com.mandarinkafe.mandarin.menu.data.dto.MenuIdResponse
import com.mandarinkafe.mandarin.menu.data.dto.MenuRequest
import com.mandarinkafe.mandarin.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.menu.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.menu.data.dto.OrganizationsResponse

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IkkoApiService {
    @POST("/api/1/access_token")
    suspend fun authenticate(@Body request: AuthRequest): AuthResponse

    @POST("/api/1/organizations")
    suspend fun getOrganizations(
        @Header("Authorization") token: String,
        @Body body: OrganizationsRequest
    ): OrganizationsResponse

    @POST("api/2/menu")
    suspend fun getMenuId(
        @Header("Authorization") token: String,
         ): MenuIdResponse //TODO

    @POST("api/2/menu/by_id")
    suspend  fun getMenuById(
        @Header("Authorization") token: String,
        @Body body: MenuRequest
    ): MenuResponse

}

