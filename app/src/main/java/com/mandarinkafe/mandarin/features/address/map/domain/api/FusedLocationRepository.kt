package com.mandarinkafe.mandarin.features.address.map.domain.api

import com.mandarinkafe.mandarin.features.address.map.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

interface FusedLocationRepository {
    suspend fun getCurrentLocation(): Resource<GeoPoint>
}