package com.mandarinkafe.mandarin.features.order.presentation.models

import androidx.compose.runtime.Stable
import com.yandex.mapkit.geometry.Point

@Stable
data class UiAddress(
    val point: Point? = null,
    val addressMain: String = "",
    val isPrivateHouse: Boolean = false,
    val apartmentNumber: String = "",
    val apartmentEntrance: String = "",
    val apartmentFloor: String = "",
    val apartmentIntercom: String = "",
    val addressComment: String = "",
)