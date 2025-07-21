package com.mandarinkafe.mandarin.core.data.network

import com.mandarinkafe.mandarin.core.data.dto.PointResponse
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.NetworkMonitor
import com.yandex.mapkit.geometry.Point

class NominatimNetworkClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val nominatimApiService: NominatimApiService,
) : NominatimNetworkClient {

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun getCoordinatesFromAddress(address: String): Response {
        return try {
            val response = nominatimApiService.searchAddress(address)
            val firstResult = response.firstOrNull()
            PointResponse(point = firstResult?.let {
                Point(it.lat.toDouble(), it.lon.toDouble())
            })
        } catch (e: Exception) {
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}