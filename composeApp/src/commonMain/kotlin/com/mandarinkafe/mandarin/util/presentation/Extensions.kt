package com.mandarinkafe.mandarin.util.presentation

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import kotlin.math.abs

fun GeoPoint.isSameAs(other: GeoPoint, epsilon: Double = 1e-6): Boolean {
    val point = this
    val otherPoint = other
    return abs(point.latitude - otherPoint.latitude) < epsilon &&
            abs(point.longitude - otherPoint.longitude) < epsilon
}
