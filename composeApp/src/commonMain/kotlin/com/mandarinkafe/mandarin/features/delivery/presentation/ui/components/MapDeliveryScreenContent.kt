package com.mandarinkafe.mandarin.features.delivery.presentation.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea

@Composable
expect fun MapDeliveryScreenContent(
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    isError: Boolean,
    initLocation: GeoPoint,
    userLocation: GeoPoint?,
    onCameraMoved: (GeoPoint) -> Unit,
    locationChosen: Boolean
)