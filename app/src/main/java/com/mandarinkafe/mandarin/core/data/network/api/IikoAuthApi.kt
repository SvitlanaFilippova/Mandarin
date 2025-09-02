package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsResponse
import retrofit2.http.Body

import retrofit2.http.POST

interface IikoAuthApi {
    @POST("/api/1/organizations")
    suspend fun getOrganizations(
        @Body body: OrganizationsRequest
    ): OrganizationsResponse
}