package com.mandarinkafe.mandarin.features.location.domain.impl

import com.mandarinkafe.mandarin.features.location.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.features.location.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.location.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

class GetCurrentLocationUseCaseImpl(private val repository: FusedLocationRepository) :
    GetCurrentLocationUseCase {
    override suspend operator fun invoke(): Resource<GeoPoint> {
        return repository.getCurrentLocation()
    }
}