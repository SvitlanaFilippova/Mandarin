package com.mandarinkafe.mandarin.features.address.address.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.Constants.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.RoundedButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapWithButtons(
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
            it.mapWindow.map.addCameraListener(cameraListener)
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

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Dimens.MarginSmall8),
        ) {
            // Кнопка "Вернуться к стартовой позиции"
            onBackToInitLocationClick?.let {
                RoundedButton(
                    onClick = onBackToInitLocationClick,
                    painter = painterResource(R.drawable.ic_undo),
                    contentDescriptionResId = R.string.to_my_location
                )
            }

            // Кнопка "Вернуться к позиции пользователя"
            onBackToUserLocationClick?.let {
                RoundedButton(
                    modifier = Modifier.padding(top = Dimens.MarginSmall8),
                    onClick = onBackToUserLocationClick,
                    painter = painterResource(R.drawable.ic_my_location),
                    contentDescriptionResId = R.string.to_my_location
                )
            }

            RoundedButton(
                modifier = Modifier.padding(top = Dimens.MarginSmall8),
                onClick = { changeZoom(mapView = mapView, delta = +1f) },
                painter = painterResource(R.drawable.ic_plus),
                contentDescriptionResId = R.string.zoom_plus
            )

            RoundedButton(
                modifier = Modifier.padding(top = Dimens.MarginSmall8),
                onClick = { changeZoom(mapView = mapView, delta = -1f) },
                painter = painterResource(R.drawable.ic_minus),
                contentDescriptionResId = R.string.zoom_minus
            )

        }

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

        // Кнопка "Доставить сюда"
        onDeliverHereClick?.let {
            ButtonWithText(
                modifier = Modifier
                    .padding(Dimens.MarginBig24)
                    .align(Alignment.BottomCenter),
                shouldBeActive = locationChosen,
                textResID = R.string.deliver_to_this_location,
                onClick = onDeliverHereClick
            )
        }

    }
}

private fun changeZoom(mapView: MapView?, delta: Float) {
    val position = mapView?.mapWindow?.map?.cameraPosition ?: return
    val newZoom = (position.zoom + delta).coerceIn(2f, 20f)

    mapView.mapWindow.map.move(
        CameraPosition(
            position.target,
            newZoom,
            position.azimuth,
            position.tilt
        ),
        Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
        null
    )
}
