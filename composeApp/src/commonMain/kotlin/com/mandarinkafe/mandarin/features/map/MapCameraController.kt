package com.mandarinkafe.mandarin.features.map

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

/**
 * Интерфейс для управления камерой карты.
 * Позволяет абстрагировать платформо-специфичную логику перемещения камеры.
 */
interface MapCameraController {
    /**
     * Перемещает камеру к указанной точке
     * @param point координаты для перемещения камеры
     * @param zoom уровень масштабирования (опционально)
     */
    fun moveCamera(point: GeoPoint, zoom: Float? = null)
}
