package com.mandarinkafe.mandarin.features.address.map.domain.impl

import com.mandarinkafe.mandarin.features.address.map.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.features.address.map.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.map.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

class GetCurrentLocationUseCaseImpl(private val repository: FusedLocationRepository) :
    GetCurrentLocationUseCase {
    override suspend operator fun invoke(): Resource<GeoPoint> {
        return repository.getCurrentLocation()
    }
}