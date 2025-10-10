package com.mandarinkafe.mandarin.features.address.address.domain.api

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

interface FusedLocationRepository {
    suspend fun getCurrentLocation(): Resource<GeoPoint>
}