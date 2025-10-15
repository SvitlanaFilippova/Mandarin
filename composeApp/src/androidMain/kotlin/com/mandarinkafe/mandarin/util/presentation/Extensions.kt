package com.mandarinkafe.mandarin.util.presentation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.yandex.mapkit.geometry.Point
import kotlin.math.abs

actual fun Any.isSameAs(other: Any, epsilon: Double): Boolean {
    val point = this as Point
    val otherPoint = other as Point
    return abs(point.latitude - otherPoint.latitude) < epsilon &&
            abs(point.longitude - otherPoint.longitude) < epsilon
}

fun Context.openGeoLocation(address: String) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        "geo:0,0?q=$address".toUri()
    )
    this.startActivity(intent)
}
