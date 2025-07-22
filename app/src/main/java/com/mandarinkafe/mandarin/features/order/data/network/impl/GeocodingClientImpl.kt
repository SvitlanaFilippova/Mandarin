package com.mandarinkafe.mandarin.features.order.data.network.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.PointResponse
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.order.data.network.GeocodingClient
import com.mandarinkafe.mandarin.features.order.data.network.NominatimApiService
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.NetworkMonitor
import com.yandex.mapkit.geometry.Point

class GeocodingClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val nominatimApiService: NominatimApiService,
) : GeocodingClient {

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun getCoordinatesFromAddress(address: String): Response {
        Log.d("DEBUG ORDER", "GeocodingClientImpl, getCoordinatesFromAddress called with: $address")

        if (!isConnected()) {
            Log.d("DEBUG ORDER", "GeocodingClientImpl, no internet")
            return Response().apply { resultCode = Constants.NO_CONNECTION }
        }

        return try {
            val response = nominatimApiService.searchAddress(address)
            Log.d("DEBUG ORDER", "GeocodingClientImpl, Retrofit response: $response")

            val firstResult = response.firstOrNull()
            Log.d("DEBUG ORDER", "GeocodingClientImpl, firstResult: $firstResult")

            PointResponse(point = firstResult?.let {
                Point(it.lat.toDouble(), it.lon.toDouble())
            })
        } catch (e: Exception) {
            Log.e("DEBUG ORDER", "GeocodingClientImpl, exception: ${e.message}", e)
            Response().apply { resultCode = Constants.HTTP_SERVER_ERROR }
        }
    }
}