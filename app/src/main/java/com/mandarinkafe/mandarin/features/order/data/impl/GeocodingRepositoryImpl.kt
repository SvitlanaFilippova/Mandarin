package com.mandarinkafe.mandarin.features.order.data.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.PointResponse
import com.mandarinkafe.mandarin.features.order.data.network.GeocodingClient
import com.mandarinkafe.mandarin.features.order.domain.api.GeocodingRepository
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.geometry.Point

class GeocodingRepositoryImpl(private val networkClient: GeocodingClient) : GeocodingRepository {
    override suspend fun getCoordinatesFromAddress(address: String): Resource<Point> {
        Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, getCoordinatesFromAddress started")

        val response = try {
            Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, calling networkClient...")
            networkClient.getCoordinatesFromAddress(address).also {
                Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, got response: $it")
            }
        } catch (e: Exception) {
            Log.e("DEBUG ORDER", "GeocodingRepositoryImpl, exception: ${e.message}", e)
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }

        if (response.resultCode == NO_CONNECTION) {
            Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, no internet connection")
            return Resource.ErrorNoInternet()
        }

        val point = (response as? PointResponse)?.point
        Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, extracted point: $point")

        return if (point == null) {
            Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, point is null")
            Resource.ErrorEmptyData()
        } else {
            Log.d("DEBUG ORDER", "GeocodingRepositoryImpl, success")
            Resource.Success(point)
        }
    }
}