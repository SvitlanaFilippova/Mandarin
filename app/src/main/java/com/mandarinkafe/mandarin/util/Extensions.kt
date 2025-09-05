package com.mandarinkafe.mandarin.util

import com.yandex.mapkit.geometry.Point

fun Point.isSameAs(other: Point, epsilon: Double = 1e-6): Boolean {
    return kotlin.math.abs(latitude - other.latitude) < epsilon &&
            kotlin.math.abs(longitude - other.longitude) < epsilon
}
