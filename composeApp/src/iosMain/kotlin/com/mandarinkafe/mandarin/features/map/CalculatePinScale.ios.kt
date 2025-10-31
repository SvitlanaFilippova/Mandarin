package com.mandarinkafe.mandarin.features.map

import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_MAX_IOS
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_MIN_IOS
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_ZOOM_MAX
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_ZOOM_MIN

actual fun calculatePinScale(zoom: Float): Float {
    val normalizedZoom = (zoom - PIN_SCALE_ZOOM_MIN) / (PIN_SCALE_ZOOM_MAX - PIN_SCALE_ZOOM_MIN)
    val clampedZoom = normalizedZoom.coerceIn(0f, 1f)
    return PIN_SCALE_MIN_IOS + (PIN_SCALE_MAX_IOS - PIN_SCALE_MIN_IOS) * clampedZoom
}