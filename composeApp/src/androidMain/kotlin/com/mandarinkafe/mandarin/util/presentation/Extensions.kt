package com.mandarinkafe.mandarin.util.presentation

import com.yandex.mapkit.geometry.Point
import kotlin.math.abs

actual fun Any.isSameAs(other: Any, epsilon: Double): Boolean {
    val point = this as Point
    val otherPoint = other as Point
    return abs(point.latitude - otherPoint.latitude) < epsilon &&
            abs(point.longitude - otherPoint.longitude) < epsilon
}
