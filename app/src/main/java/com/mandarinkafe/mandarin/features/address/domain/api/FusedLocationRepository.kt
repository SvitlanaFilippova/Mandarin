package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.features.address.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

interface FusedLocationRepository {
    suspend fun getCurrentLocation(): Resource<GeoPoint>
}