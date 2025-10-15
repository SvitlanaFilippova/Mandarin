package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import android.content.Intent
import android.graphics.PointF
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.CustomMapView
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.OurAddressesCard
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_ANIMATION_DURATION
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_AZIMUTH
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_TILT
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_MAX_ZOOM
import com.mandarinkafe.mandarin.util.ConstantsMap.MAP_MIN_ZOOM
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_X
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_Y
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_LABEL_CAFE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_LABEL_PIZZERIA
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_OPACITY
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_TEXT_ALPHA
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_TEXT_OFFSET
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_TEXT_SIZE
import com.mandarinkafe.mandarin.util.presentation.openGeoLocation
import com.mandarinkafe.mandarin.util.presentation.ui.components.BindMapViewToLifecycle
import com.mandarinkafe.mandarin.util.presentation.ui.components.InfoCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.RoundedButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun ContactsScreen(onBackClick: () -> Boolean) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val addresses = listOf(
        R.string.pickup_cafe_address,
        R.string.pickup_pizzeria_address
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        // Заголовок экрана
        ScreenTitleWithBackButton(
            name = stringResource(R.string.contacts_screen_title),
            onBackClick = { onBackClick() },
        )

        // График
        InfoCard(
            iconPainter = painterResource(R.drawable.ic_clock),
            title = stringResource(R.string.working_hours_title),
            lines = listOf(
                stringResource(R.string.working_hours_value) to null
            )
        )

        // Телефон
        InfoCard(
            iconVector = Icons.Default.Phone,
            title = stringResource(R.string.phone_title),
            lines = listOf(
                stringResource(R.string.cafe_phone_number) to {
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        "tel:${context.getString(R.string.cafe_phone_number)}".toUri()
                    )
                    context.startActivity(intent)
                }
            )
        )

        OurAddressesCard(
            lines = addresses.map { resId ->
                stringResource(resId) to {
                    context.openGeoLocation(context.getString(resId))
                }
            }
        )

        MapWithCafePins(
            mapView = mapView,
            onMapReady = {
                mapView = it
                moveCamera(mapView = mapView)
            },
            onBackToInitLocationClick = {
                moveCamera(mapView = mapView)
            }
        )
        BindMapViewToLifecycle(mapView)
    }
}

@Composable
private fun MapWithCafePins(
    mapView: MapView?,
    onMapReady: (MapView) -> Unit,
    onBackToInitLocationClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CustomMapView(context)
            }
        ) {
            onMapReady(it)
            addPins(it)
        }

        // Блок с кнопками для управления картой
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Dimens.MarginSmall8),
        ) {
            // Кнопка "Вернуться к стартовой позиции"
            RoundedButton(
                onClick = onBackToInitLocationClick,
                painter = painterResource(R.drawable.ic_undo),
                contentDescriptionResId = R.string.to_init_location
            )

            // Кнопка "Приблизить"
            RoundedButton(
                modifier = Modifier.padding(top = Dimens.MarginSmall8),
                onClick = { changeZoom(mapView = mapView, delta = +1f) },
                painter = painterResource(R.drawable.ic_plus),
                contentDescriptionResId = R.string.zoom_plus
            )

            // Кнопка "Отдалить"
            RoundedButton(
                modifier = Modifier.padding(top = Dimens.MarginSmall8),
                onClick = { changeZoom(mapView = mapView, delta = -1f) },
                painter = painterResource(R.drawable.ic_minus),
                contentDescriptionResId = R.string.zoom_minus
            )

        }
    }
}

private fun changeZoom(mapView: MapView?, delta: Float) {
    val position = mapView?.mapWindow?.map?.cameraPosition ?: return
    val newZoom = (position.zoom + delta).coerceIn(MAP_MIN_ZOOM, MAP_MAX_ZOOM)

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

private fun moveCamera(point: Point? = null, mapView: MapView?) {
    val mandarinInitPoint = Point(MANDARIN_CENTER_LATITUDE, MANDARIN_CENTER_LONGITUDE)

    mapView?.mapWindow?.map?.move(
        CameraPosition(
            point ?: mandarinInitPoint,
            MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN,
            MAP_DEFAULT_AZIMUTH,
            MAP_DEFAULT_TILT
        ),
        Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
        null
    )
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
