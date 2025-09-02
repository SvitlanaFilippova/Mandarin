package com.mandarinkafe.mandarin.util

import com.yandex.mapkit.geometry.Point
import kotlin.math.pow
import kotlin.math.round

fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}

fun Point.isSameAs(other: Point, epsilon: Double = 1e-6): Boolean {
    return kotlin.math.abs(latitude - other.latitude) < epsilon &&
            kotlin.math.abs(longitude - other.longitude) < epsilon
}

