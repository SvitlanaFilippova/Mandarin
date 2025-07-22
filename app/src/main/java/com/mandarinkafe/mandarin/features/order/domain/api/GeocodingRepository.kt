package com.mandarinkafe.mandarin.features.order.domain.api

import com.mandarinkafe.mandarin.util.Resource
import com.yandex.mapkit.geometry.Point

interface GeocodingRepository {
    suspend fun getCoordinatesFromAddress(address: String): Resource<Point>
}