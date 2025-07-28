package com.mandarinkafe.mandarin.features.address.address.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.RoundedButton
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapWithButtons(
    mapView: MapView?,
    deliveryAreas: List<UiDeliveryArea>,
    displayAddress: String?,
    deliveryArea: UiDeliveryArea?,
    isLoading: Boolean,
    locationChosen: Boolean,
    isError: Boolean,
    onCameraMoved: (Point) -> Unit,
    onDeliverHereClick: () -> Unit,
    onMapReady: (MapView) -> Unit,
    onBackToInitLocationClick: (() -> Unit)?,
    onBackToUserLocationClick: (() -> Unit)?
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
    )
    {
        AndroidView(
            // Карта
            modifier = Modifier.fillMaxSize(),
            factory = { MapView(it) },
        ) {
            it.mapWindow.map.addCameraListener(cameraListener)
            onMapReady(it)
        }
        if (displayAddress != null && !isLoading) {
            // Окно с информацией о текущей зоне доставки
            DeliveryAreaInfo(
                modifier = Modifier.align(Alignment.TopCenter),
                deliveryArea = deliveryArea
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = Dimens.MarginHuge80, end = Dimens.MarginSmall8),
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


