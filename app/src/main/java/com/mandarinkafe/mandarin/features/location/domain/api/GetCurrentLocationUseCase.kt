package com.mandarinkafe.mandarin.features.location.domain.api

import com.mandarinkafe.mandarin.features.location.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

interface GetCurrentLocationUseCase {
    suspend operator fun invoke(): Resource<GeoPoint>
}