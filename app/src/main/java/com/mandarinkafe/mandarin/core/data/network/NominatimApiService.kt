package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.features.order.data.dto.NominatimResponse
import com.mandarinkafe.mandarin.util.Constants.ACCEPT_LANGUAGE_RU
import com.mandarinkafe.mandarin.util.Constants.FORMAT_JSON
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("search")
    suspend fun searchAddress(
        @Query("q") address: String,
        @Query("format") format: String = FORMAT_JSON,
        @Query("limit") limit: Int = 1,
        @Query("accept-language") language: String = ACCEPT_LANGUAGE_RU
    ): List<NominatimResponse>
}
