package com.mandarinkafe.mandarin.features.address.data.impl

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.util.Resource

class FusedLocationRepositoryImpl : FusedLocationRepository {
    override suspend fun getCurrentLocation(): Resource<GeoPoint> {
        // TODO: Implement iOS location using CoreLocation
        return Resource.ErrorOther("Location not implemented for iOS yet")
    }
}

