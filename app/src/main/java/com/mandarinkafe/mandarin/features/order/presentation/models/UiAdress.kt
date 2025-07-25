package com.mandarinkafe.mandarin.features.order.presentation.models

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint

@Stable
data class UiAddress(
    val point: GeoPoint? = null,
    val streetAndBuilding: String = "",
    val isPrivateHouse: Boolean = false,
    val apartmentNumber: String = "",
    val entrance: String = "",
    val floor: String = "",
    val intercom: String = "",
    val comment: String = "",
)