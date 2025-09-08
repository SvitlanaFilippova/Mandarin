package com.mandarinkafe.mandarin.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

import com.yandex.mapkit.geometry.Point

fun Point.isSameAs(other: Point, epsilon: Double = 1e-6): Boolean {
    return kotlin.math.abs(latitude - other.latitude) < epsilon &&
            kotlin.math.abs(longitude - other.longitude) < epsilon
}

fun Context.openGeoLocation(address: String) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        "geo:0,0?q=$address".toUri()
    )
    this.startActivity(intent)
}
