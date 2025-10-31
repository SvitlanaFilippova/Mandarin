package com.mandarinkafe.mandarin.features.map

/**
 * Вычисляет масштаб пинов на основе текущего зума карты
 * @param zoom текущий зум карты
 * @return масштаб пина от PIN_SCALE_MIN до PIN_SCALE_MAX
 */
expect fun calculatePinScale(zoom: Float): Float
