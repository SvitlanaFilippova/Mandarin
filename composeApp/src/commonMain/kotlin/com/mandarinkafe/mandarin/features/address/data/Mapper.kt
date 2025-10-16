package com.mandarinkafe.mandarin.features.address.data

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

expect object Mapper {
    fun GeoPoint.toYandexPoint(): Any
    fun Any.toGeoPoint(): GeoPoint
}

