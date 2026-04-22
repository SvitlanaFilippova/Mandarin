package com.mandarinkafe.mandarin.util.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import kotlin.math.abs

fun GeoPoint.isSameAs(other: GeoPoint, epsilon: Double = 1e-6): Boolean {
    val point = this
    val otherPoint = other
    return abs(point.latitude - otherPoint.latitude) < epsilon &&
            abs(point.longitude - otherPoint.longitude) < epsilon
}

fun Modifier.clearFocusOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current

    pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                focusManager.clearFocus()
            }
        )
    }
}