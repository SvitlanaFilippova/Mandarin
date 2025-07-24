package com.mandarinkafe.mandarin.features.address.map.domain.models

import com.yandex.mapkit.geometry.Point

fun GeoPoint.toYandexPoint(): Point {
    return Point(latitude, longitude)
}

fun Point.toGeoPoint(): GeoPoint {
    return GeoPoint(latitude, longitude)
}