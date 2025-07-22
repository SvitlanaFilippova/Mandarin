package com.mandarinkafe.mandarin.features.order.data.network

import com.mandarinkafe.mandarin.features.order.data.dto.NominatimResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("search")
    suspend fun searchAddress(
        @Query("q") address: String,
        @Query("format") format: String = FORMAT_JSON,
        @Query("limit") limit: Int = 1,
        @Query("bounded") bounded: Int = 1,
        @Query("viewbox") viewbox: String = VIEWBOX_CHG,
        @Query("countrycodes") countryCodes: String = COUNTRY_CODE,
        @Query("accept-language") language: String = ACCEPT_LANGUAGE_RU
    ): List<NominatimResponse>

    private companion object {
        const val VIEWBOX_CHG = "37.92824,56.17193,38.89435,55.78159"
        const val COUNTRY_CODE = "ru"
        const val ACCEPT_LANGUAGE_RU = "ru"
        const val FORMAT_JSON = "json"
    }
}
