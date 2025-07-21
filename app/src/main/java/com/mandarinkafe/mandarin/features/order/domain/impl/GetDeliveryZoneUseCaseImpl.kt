package com.mandarinkafe.mandarin.features.order.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.core.domain.models.DeliveryArea
import com.mandarinkafe.mandarin.features.order.domain.api.GetDeliveryZoneUseCase
import com.yandex.mapkit.geometry.Point

class GetDeliveryZoneUseCaseImpl(
    private val deliveryAreaRepository: DeliveryAreaRepository
) : GetDeliveryZoneUseCase {

    override fun invoke(location: Point): List<DeliveryArea> {
        val areas = deliveryAreaRepository.getAllAreas()
        return areas.filter { area ->
            isPointInPolygon(location, area.polygon) &&
                    (area.parentArea?.let { !isPointInPolygon(location, it) } != false)
        }
    }

    // Определяет, находится ли точка внутри полигона (true — внутри, false — снаружи)
    // Используется алгоритм Ray Casting: горизонтальный луч от точки проверяется на количество пересечений с границами полигона
    private fun isPointInPolygon(point: Point, polygon: List<Point>): Boolean {
        var intersectCount = 0
        for (i in polygon.indices) {
            val j = (i + 1) % polygon.size // Следующая вершина, с учётом замыкания полигона
            val p1 = polygon[i]
            val p2 = polygon[j]

            // Если горизонтальный луч от точки пересекает сторону полигона — увеличиваем счётчик
            if (rayIntersectsSegment(point, p1, p2)) {
                intersectCount++
            }
        }
        // Если число пересечений нечётное — точка внутри
        return (intersectCount % 2 == 1)
    }

    // Проверяет, пересекает ли горизонтальный луч, идущий вправо от точки p, отрезок (p1, p2)
    private fun rayIntersectsSegment(p: Point, p1: Point, p2: Point): Boolean {
        val px = p.longitude // X-координата точки
        var py = p.latitude  // Y-координата точки

        val x1 = p1.longitude
        val y1 = p1.latitude
        val x2 = p2.longitude
        val y2 = p2.latitude

        // Упорядочиваем точки отрезка по Y, чтобы всегда y1 <= y2
        if (y1 > y2) return rayIntersectsSegment(p, p2, p1)

        // Обработка случая, когда точка лежит ровно на вершине — слегка поднимаем её вверх
        if (py == y1 || py == y2) py += 0.0000001

        // Если точка выше, ниже или правее отрезка — пересечения нет
        if (py > y2 || py < y1 || px > maxOf(x1, x2)) return false

        // Если точка левее всего отрезка — считаем, что луч точно пересекает
        if (px < minOf(x1, x2)) return true

        // Сравниваем углы наклона:
        // red — наклон отрезка полигона, blue — наклон от точки p1 до точки p
        val red = if (x1 != x2) (y2 - y1) / (x2 - x1) else Double.MAX_VALUE
        val blue = if (x1 != px) (py - y1) / (px - x1) else Double.MAX_VALUE

        // Если наклон луча >= наклона отрезка — считается, что пересекли
        return blue >= red
    }

}