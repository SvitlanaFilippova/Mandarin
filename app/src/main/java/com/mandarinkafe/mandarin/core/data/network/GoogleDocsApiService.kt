package com.mandarinkafe.mandarin.core.data.network

import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GoogleDocsApiService {
    @GET
    @Streaming
    suspend fun getCsv(@Url url: String): String
}