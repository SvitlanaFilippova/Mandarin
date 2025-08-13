package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.AuthResponse
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IikoAuthApi {
    @POST("/api/1/access_token")
    suspend fun authenticate(@Body request: AuthRequest): AuthResponse

    @POST("/api/1/organizations")
    suspend fun getOrganizations(
        @Header("Authorization") token: String,
        @Body body: OrganizationsRequest
    ): OrganizationsResponse

}