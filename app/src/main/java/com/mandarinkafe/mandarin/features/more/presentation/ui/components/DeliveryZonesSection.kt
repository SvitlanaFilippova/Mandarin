package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.MapWithButtons
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.Constants.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.Constants.MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN
import com.mandarinkafe.mandarin.util.presentation.ui.components.BindMapViewToLifecycle
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun DeliveryZonesSection(
    deliveryAreas: List<UiDeliveryArea>,
    locationChosen: Boolean,
    isError: Boolean,
    isLoading: Boolean,
    deliveryArea: UiDeliveryArea?,
    displayAddress: String?,
    initLocation: Point,
    mapShouldBeVisible: Boolean,
    onCameraMoved: (Point) -> Unit,
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    LaunchedEffect(initLocation, mapView) {
        moveCamera(initLocation, mapView)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.MarginStandard16),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_map),
                    contentDescription = stringResource(R.string.delivery_areas),
                    tint = Colors.WhiteTransparent75
                )
                Column(
                    modifier = Modifier
                        .padding(start = Dimens.MarginStandard16, bottom = Dimens.MarginStandard16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
                ) {
                    Text(
                        text = stringResource(R.string.delivery_areas),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.delivery_zones_text),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (mapShouldBeVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.MapOnDeliveryScreenHeight)
                        .padding(bottom = Dimens.MarginStandard16)
                ) {
                    MapWithButtons(
                        mapView = mapView,
                        deliveryAreas = deliveryAreas,
                        displayAddress = displayAddress,
                        deliveryArea = deliveryArea,
                        isLoading = isLoading,
                        isError = isError,
                        onMapReady = { mapView = it },
                        onCameraMoved = onCameraMoved,
                        onBackToInitLocationClick = {
                            moveCamera(
                                point = initLocation,
                                mapView = mapView
                            )
                        },
                        locationChosen = locationChosen
                    )
                }

            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
            ) {
                deliveryAreas.forEach { area ->
                    DeliveryAreaCompactInfo(
                        modifier = Modifier
                            .weight(1f),
                        deliveryArea = area
                    )
                }
            }
        }
    }
    BindMapViewToLifecycle(mapView)
}

private fun moveCamera(point: Point?, mapView: MapView?) {
    if (point != null) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(
                point,
                MAP_DEFAULT_ZOOM_FOR_DELIVERY_SCREEN,
                MAP_DEFAULT_AZIMUTH,
                MAP_DEFAULT_TILT
            ),
            Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
            null
        )
    }
}