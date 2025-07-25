package com.mandarinkafe.mandarin.features.address.map.presentation.ui.models

import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

data class UiDeliveryArea(
    val id: Int,
    val polygon: List<GeoPoint>,
    val parentArea: List<GeoPoint>?,
    val deliveryPrice: Int,
    val freeDeliveryThreshold: Int,
    val color: Color
)