package com.mandarinkafe.mandarin.core.data.network.api

import com.mandarinkafe.mandarin.features.infrastructure.data.network.AliveTerminalGroupsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.TerminalGroupsIdsRequest
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.AliveTerminalGroupsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.TerminalGroupsIdsResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IikoTerminalApi {
    @POST("/api/1/terminal_groups")
    suspend fun getTerminalGroupsIds(
        @Header("Authorization") token: String,
        @Body body: TerminalGroupsIdsRequest
    ): TerminalGroupsIdsResponse

    @POST("/api/1/terminal_groups/is_alive")
    suspend fun getAliveTerminalGroups(
        @Header("Authorization") token: String,
        @Body body: AliveTerminalGroupsRequest
    ): AliveTerminalGroupsResponse
}