package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface NominatimNetworkClient {
    suspend fun getCoordinatesFromAddress(address: String): Response
}