package com.mandarinkafe.mandarin.features.address.map.domain.api

import com.mandarinkafe.mandarin.features.address.map.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

interface GetCurrentLocationUseCase {
    suspend operator fun invoke(): Resource<GeoPoint>
}