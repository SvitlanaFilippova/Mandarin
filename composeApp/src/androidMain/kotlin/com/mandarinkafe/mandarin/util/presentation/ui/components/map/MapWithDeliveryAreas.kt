package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.mapview.MapView
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MapWithDeliveryAreas(
    mapView: MapView?,
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    onMapReady: (MapView) -> Unit,
    isError: Boolean,
    onCameraMoved: (Point) -> Unit,
    onDeliverHereClick: (() -> Unit)? = null,
    locationChosen: Boolean,
    onBackToInitLocationClick: (() -> Unit)?,
    onBackToUserLocationClick: (() -> Unit)? = null
) {
    val cameraListener = remember {
        CameraListener { _, cameraPosition, _, finished ->
            if (finished) {
                onCameraMoved(cameraPosition.target)
            }
        }
    }

    mapView?.let {
        DeliveryAreasOnMap(
            mapView = it,
            deliveryAreas = deliveryAreas
        )
    }

    // Добавляем слушатель камеры
    LaunchedEffect(mapView) {
        mapView?.mapWindow?.map?.addCameraListener(cameraListener)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Dimens.MarginSmall8)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CustomMapView(context)
            }
        ) {
            onMapReady(it)
        }

        // Окно с информацией о текущей зоне доставки
        AnimatedVisibility(
            visible = displayAddress != null && !isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DeliveryAreaInfo(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                deliveryArea = deliveryArea
            )
        }

        MapButtons(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onBackToInitLocationClick = onBackToInitLocationClick,
            onBackToUserLocationClick = onBackToUserLocationClick,
            onZoomIn = { changeZoom(mapView = mapView, delta = +1f) },
            onZoomOut = { changeZoom(mapView = mapView, delta = -1f) }
        )

        // Центральный маркер
        val offset = remember { -Dimens.MapPinSize / 2 }
        ChosenLocationPin(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = offset),
            isLoading = isLoading,
            addressFound = locationChosen,
            isError = isError
        )

    }
}
