package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.InfoCard
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.MakeCall
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ContactsScreen(onBackClick: () -> Unit) {

    val addresses = listOf(
        MR.strings.pickup_cafe_address,
        MR.strings.pickup_pizzeria_address
    )
    
    val phoneNumber = stringResource(MR.strings.cafe_phone_number)
    var shouldMakePhoneCall by remember { mutableStateOf(false) }
    
    if (shouldMakePhoneCall) {
        MakeCall(
            phoneNumber = phoneNumber
        )
        LaunchedEffect(Unit) {
            shouldMakePhoneCall = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        // Заголовок экрана
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.contacts_screen_title),
            onBackClick = { onBackClick() },
        )

        // График
        InfoCard(
            iconPainter = painterResource(MR.images.ic_clock),
            title = stringResource(MR.strings.working_hours_title),
            lines = listOf(
                stringResource(MR.strings.working_hours_value) to null
            )
        )

        // Телефон
        InfoCard(
            iconPainter = painterResource(MR.images.ic_phone),
            title = stringResource(MR.strings.phone_title),
            lines = listOf(
                phoneNumber to {
                    shouldMakePhoneCall = true
                }
            )
        )


// TODO всё что ниже - тащить в платформенные реализации через expect/actual

//        val context = LocalContext.current
//        var mapView by remember { mutableStateOf<MapView?>(null) }
//
//        OurAddressesCard(
//            lines = addresses.map { resId ->
//                stringResource(resId) to {
//                    context.openGeoLocation(context.getString(resId))
//                }
//            }
//        )
//
//        MapWithCafePins(
//            mapView = mapView,
//            onMapReady = {
//                mapView = it
//                moveCamera(mapView = mapView)
//            },
//            onBackToInitLocationClick = {
//                moveCamera(mapView = mapView)
//            }
//        )
//        BindMapViewToLifecycle(mapView)
//    }
//}
//
//@Composable
//private fun MapWithCafePins(
//    mapView: MapView?,
//    onMapReady: (MapView) -> Unit,
//    onBackToInitLocationClick: () -> Unit,
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .clip(RoundedCornerShape(Dimens.CornerRadius8))
//    ) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { context ->
//                CustomMapView(context)
//            }
//        ) {
//            onMapReady(it)
//            addPins(it)
//        }
//
//        // Блок с кнопками для управления картой
//        Column(
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(end = Dimens.MarginSmall8),
//        ) {
//            // Кнопка "Вернуться к стартовой позиции"
//            RoundedButton(
//                onClick = onBackToInitLocationClick,
//                painter = painterResource(MR.images.ic_undo),
//                contentDescription = stringResource(MR.strings.to_init_location)
//            )
//
//            // Кнопка "Приблизить"
//            RoundedButton(
//                modifier = Modifier.padding(top = Dimens.MarginSmall8),
//                onClick = { changeZoom(mapView = mapView, delta = +1f) },
//                painter = painterResource(MR.images.ic_plus),
//                contentDescription = stringResource(MR.strings.zoom_plus)
//            )
//
//            // Кнопка "Отдалить"
//            RoundedButton(
//                modifier = Modifier.padding(top = Dimens.MarginSmall8),
//                onClick = { changeZoom(mapView = mapView, delta = -1f) },
//                painter = painterResource(MR.images.ic_minus),
//                contentDescription =stringResource(MR.strings.zoom_minus)
//            )
//
//        }
//    }
//}
//
//private fun changeZoom(mapView: MapView?, delta: Float) {
//    val position = mapView?.mapWindow?.map?.cameraPosition ?: return
//    val newZoom = (position.zoom + delta).coerceIn(MAP_MIN_ZOOM, MAP_MAX_ZOOM)
//
//    mapView.mapWindow.map.move(
//        CameraPosition(
//            position.target,
//            newZoom,
//            position.azimuth,
//            position.tilt
//        ),
//        Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
//        null
//    )
//}
//
//private fun moveCamera(point: Point? = null, mapView: MapView?) {
//    val mandarinInitPoint = Point(MANDARIN_CENTER_LATITUDE, MANDARIN_CENTER_LONGITUDE)
//
//    mapView?.mapWindow?.map?.move(
//        CameraPosition(
//            point ?: mandarinInitPoint,
//            MAP_DEFAULT_ZOOM_FOR_ADDRESS_SCREEN,
//            MAP_DEFAULT_AZIMUTH,
//            MAP_DEFAULT_TILT
//        ),
//        Animation(Animation.Type.SMOOTH, MAP_ANIMATION_DURATION),
//        null
//    )
//}
//
//private fun addPins(mapView: MapView?) {
//    mapView?.mapWindow?.map?.mapObjects?.let { mapObjects ->
//        val pinsCollection = mapObjects.addCollection()
//
//        val cafePoint = Point(MANDARIN_CAFE_LATITUDE, MANDARIN_CAFE_LONGITUDE)
//        val pizzeriaPoint = Point(MANDARIN_PIZZERIA_LATITUDE, MANDARIN_PIZZERIA_LONGITUDE)
//        val pinIcon = ImageProvider.fromResource(mapView.context, R.drawable.ic_pin_black)
//
//        val iconStyle = IconStyle().apply {
//            anchor = PointF(PIN_ANCHOR_X, PIN_ANCHOR_Y)
//            scale = PIN_SCALE
//        }
//
//        val textStyle = TextStyle().apply {
//            size = PIN_TEXT_SIZE
//            color = Colors.DarkGrey.copy(alpha = PIN_TEXT_ALPHA).toArgb()
//            placement = TextStyle.Placement.TOP
//            offset = PIN_TEXT_OFFSET
//        }
//
//        addPin(pinsCollection, cafePoint, pinIcon, iconStyle, PIN_LABEL_CAFE, textStyle)
//        addPin(pinsCollection, pizzeriaPoint, pinIcon, iconStyle, PIN_LABEL_PIZZERIA, textStyle)
//    }
//}
//
//private fun addPin(
//    collection: MapObjectCollection,
//    point: Point,
//    icon: ImageProvider,
//    iconStyle: IconStyle,
//    label: String,
//    textStyle: TextStyle
//) {
//    collection.addPlacemark().apply {
//        geometry = point
//        opacity = PIN_OPACITY
//        setIcon(icon, iconStyle)
//        setText(label)
//        setTextStyle(textStyle)

    }
}
