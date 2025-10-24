package com.mandarinkafe.mandarin.util

import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_MAX
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_MIN
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_ZOOM_MAX
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE_ZOOM_MIN

/**
 * Вычисляет масштаб пинов на основе текущего зума карты
 * @param zoom текущий зум карты
 * @return масштаб пина от PIN_SCALE_MIN до PIN_SCALE_MAX
 */
fun calculatePinScale(zoom: Float): Float {
    val normalizedZoom = (zoom - PIN_SCALE_ZOOM_MIN) / (PIN_SCALE_ZOOM_MAX - PIN_SCALE_ZOOM_MIN)
    val clampedZoom = normalizedZoom.coerceIn(0f, 1f)
    return PIN_SCALE_MIN + (PIN_SCALE_MAX - PIN_SCALE_MIN) * clampedZoom
}
