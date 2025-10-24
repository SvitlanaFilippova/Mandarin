package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mandarinkafe.mandarin.shared.R
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CAFE_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_PIZZERIA_LONGITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_X
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_ANCHOR_Y
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_OPACITY
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_SCALE
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun CafePinsOnMap(
    mapView: MapView,
    showBigPins: Boolean,
) {
    // Используем LaunchedEffect с ключом, который включает и mapView и zoomIsClose
    LaunchedEffect(mapView, showBigPins) {
        // Очищаем старые пины
        clearCafePins(mapView)

        // Добавляем новые пины
        addCafePins(mapView, showBigPins)
    }
}

private fun clearCafePins(mapView: MapView) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    mapObjects.clear()
}

private fun addCafePins(mapView: MapView, zoomIsClose: Boolean) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    val pinsCollection = mapObjects.addCollection()

    val cafePoint = Point(MANDARIN_CAFE_LATITUDE, MANDARIN_CAFE_LONGITUDE)
    val pizzeriaPoint = Point(MANDARIN_PIZZERIA_LATITUDE, MANDARIN_PIZZERIA_LONGITUDE)

    val pinIconCafeRes = if (zoomIsClose) R.drawable.map_pin_cafe else R.drawable.map_pin_cafe_small
    val pinIconPizzaRes = if (zoomIsClose) R.drawable.map_pin_pizza else R.drawable.map_pin_pizza_small

    val pinIconCafe = ImageProvider.fromResource(mapView.context, pinIconCafeRes)
    val pinIconPizza = ImageProvider.fromResource(mapView.context, pinIconPizzaRes)

    val iconStyle = IconStyle().apply {
        anchor = PointF(PIN_ANCHOR_X, PIN_ANCHOR_Y)
        scale = PIN_SCALE
    }

    addPin(pinsCollection, cafePoint, pinIconCafe, iconStyle)
    addPin(pinsCollection, pizzeriaPoint, pinIconPizza, iconStyle)
}

private fun addPin(
    collection: MapObjectCollection,
    point: Point,
    icon: ImageProvider,
    iconStyle: IconStyle,
) {
    collection.addPlacemark().apply {
        geometry = point
        opacity = PIN_OPACITY
        setIcon(icon, iconStyle)
    }
}
