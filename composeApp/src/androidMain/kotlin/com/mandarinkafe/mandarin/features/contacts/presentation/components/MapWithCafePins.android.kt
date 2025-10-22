package com.mandarinkafe.mandarin.features.contacts.presentation.components

import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.shared.R
import com.mandarinkafe.mandarin.util.ConstantsMap
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_X
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_Y
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_LABEL_CAFE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_LABEL_PIZZERIA
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_OPACITY
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_TEXT_ALPHA
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_TEXT_OFFSET
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_TEXT_SIZE
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.CustomMapView
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.MapButtons
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.changeZoom
import com.mandarinkafe.mandarin.util.presentation.ui.components.map.moveCamera
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
actual fun MapWithCafePins() {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val mandarinInitPoint =
        Point(MANDARIN_CENTER_LATITUDE, MANDARIN_CENTER_LONGITUDE)

    val onMapReady: (MapView) -> Unit = {
        mapView = it
        moveCamera(
            mapView = mapView,
            point = mandarinInitPoint
        )
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) {
        AndroidView(
            modifier = Modifier.Companion.fillMaxSize(),
            factory = { context ->
                CustomMapView(context)
            }
        ) {
            onMapReady(it)
            addPins(it)
        }
        // Блок с кнопками для управления картой
        MapButtons(
            modifier = Modifier.Companion
                .align(Alignment.Companion.CenterEnd),

            onBackToInitLocationClick = {
                moveCamera(
                    mapView = mapView,
                    point = mandarinInitPoint
                )
            },

            onZoomIn = { changeZoom(mapView = mapView, delta = +1f) },
            onZoomOut = { changeZoom(mapView = mapView, delta = -1f) },
        )

    }
    BindMapViewToLifecycle(mapView)
}


private fun addPins(mapView: MapView?) {
    mapView?.mapWindow?.map?.mapObjects?.let { mapObjects ->
        val pinsCollection = mapObjects.addCollection()

        val cafePoint = Point(MANDARIN_CAFE_LATITUDE, MANDARIN_CAFE_LONGITUDE)
        val pizzeriaPoint = Point(MANDARIN_PIZZERIA_LATITUDE, MANDARIN_PIZZERIA_LONGITUDE)
        val pinIcon = ImageProvider.fromResource(mapView.context, R.drawable.ic_pin_black)

        val iconStyle = IconStyle().apply {
            anchor = PointF(PIN_ANCHOR_X, PIN_ANCHOR_Y)
            scale = PIN_SCALE
        }

        val textStyle = TextStyle().apply {
            size = PIN_TEXT_SIZE
            color = Colors.DarkGrey.copy(alpha = PIN_TEXT_ALPHA).toArgb()
            placement = TextStyle.Placement.TOP
            offset = PIN_TEXT_OFFSET
        }

        addPin(pinsCollection, cafePoint, pinIcon, iconStyle, PIN_LABEL_CAFE, textStyle)
        addPin(pinsCollection, pizzeriaPoint, pinIcon, iconStyle, PIN_LABEL_PIZZERIA, textStyle)
    }
}

private fun addPin(
    collection: MapObjectCollection,
    point: Point,
    icon: ImageProvider,
    iconStyle: IconStyle,
    label: String,
    textStyle: TextStyle
) {
    collection.addPlacemark().apply {
        geometry = point
        opacity = PIN_OPACITY
        setIcon(icon, iconStyle)
        setText(label)
        setTextStyle(textStyle)
    }
}