package com.mandarinkafe.mandarin.features.delivery.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea1
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea10
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea11
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea12
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea2
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea3
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea4
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea5
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea6
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea7
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea8
import com.mandarinkafe.mandarin.features.delivery.Coordinates.PlaceMarkArea9
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA1
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA10
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA11
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA12
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA2
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA3
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA4
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA5
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA6
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA7
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA8
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_AREA9
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA1
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA10
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA11
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA12
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA2
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA3
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA4
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA5
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA6
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA7
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA8
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_PRICE_FREE_AREA9
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView

@Composable
fun mapPlaceMarks(mapView: MapView): List<PlacemarkMapObject> {
    val context = LocalContext.current
    val placeMarks = remember { mutableListOf<PlacemarkMapObject>() }

    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea1,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA1,
                DELIVERY_PRICE_AREA1
            ) + context.getString(R.string.delivery_price_from_beregovaya)
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea2,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA2,
                DELIVERY_PRICE_AREA2
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea3,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA3,
                DELIVERY_PRICE_AREA3
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea4,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA4,
                DELIVERY_PRICE_AREA4
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea5,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA5,
                DELIVERY_PRICE_AREA5
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea6,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA6,
                DELIVERY_PRICE_AREA6
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea7,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA7,
                DELIVERY_PRICE_AREA7
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea8,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA8,
                DELIVERY_PRICE_AREA8
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea9,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA9,
                DELIVERY_PRICE_AREA9
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea10,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA10,
                DELIVERY_PRICE_AREA10
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea11,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA11,
                DELIVERY_PRICE_AREA11
            )
        )
    )
    placeMarks.add(
        addPlaceMark(
            mapView,
            PlaceMarkArea12,
            context.getString(
                R.string.free_delivery_at,
                DELIVERY_PRICE_FREE_AREA12,
                DELIVERY_PRICE_AREA12
            )
        )
    )
    return placeMarks
}

private fun addPlaceMark(mapView: MapView, point: Point, text: String): PlacemarkMapObject {
    val mapObjects = mapView.mapWindow?.map?.mapObjects!!
    return mapObjects.addPlacemark().apply {
        geometry = point
        setText(text)
    }
}