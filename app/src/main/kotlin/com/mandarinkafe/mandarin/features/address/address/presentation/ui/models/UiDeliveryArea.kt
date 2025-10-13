package com.mandarinkafe.mandarin.features.address.address.presentation.ui.models

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import io.github.aakira.napier.Napier

data class UiDeliveryArea(
    val id: Int,
    val polygon: List<GeoPoint>,
    val parentArea: List<GeoPoint>?,
    val deliveryPrice: Int,
    val freeDeliveryThreshold: Int,
    val color: Color
)

fun DeliveryZone.toUi(): UiDeliveryArea {
    val safeColor = try {
        Color(colorHex.toColorInt())
    } catch (e: IllegalArgumentException) {
        Napier.e(
            "Не удалось распознать цвет. Использую стандартный. Ошибка: $e"
        )
        Color.Gray
    }
    return UiDeliveryArea(
        id = id,
        polygon = polygon,
        parentArea = parentArea,
        deliveryPrice = deliveryPrice,
        freeDeliveryThreshold = freeDeliveryThreshold,
        color = safeColor
    )
}