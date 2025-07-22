package com.mandarinkafe.mandarin.features.order.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response

interface GeocodingClient {
    suspend fun getCoordinatesFromAddress(address: String): Response
}