package com.mandarinkafe.mandarin.features.location.domain.api

import com.mandarinkafe.mandarin.features.location.domain.models.GeoPoint

interface GetAddressByPointUseCase {
    suspend operator fun invoke(point: GeoPoint): String?
}