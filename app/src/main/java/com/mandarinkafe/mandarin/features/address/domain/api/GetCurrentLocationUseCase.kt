package com.mandarinkafe.mandarin.features.address.domain.api

import com.mandarinkafe.mandarin.features.address.domain.models.GeoPoint
import com.mandarinkafe.mandarin.util.Resource

interface GetCurrentLocationUseCase {
    suspend operator fun invoke(): Resource<GeoPoint>
}