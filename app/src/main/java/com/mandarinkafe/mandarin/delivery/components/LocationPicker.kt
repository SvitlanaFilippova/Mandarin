package com.mandarinkafe.mandarin.delivery.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.delivery.Coordinates.eighthArea
import com.mandarinkafe.mandarin.delivery.Coordinates.eleventhArea
import com.mandarinkafe.mandarin.delivery.Coordinates.fifthArea
import com.mandarinkafe.mandarin.delivery.Coordinates.firstArea
import com.mandarinkafe.mandarin.delivery.Coordinates.fourthArea
import com.mandarinkafe.mandarin.delivery.Coordinates.ninthArea
import com.mandarinkafe.mandarin.delivery.Coordinates.secondArea
import com.mandarinkafe.mandarin.delivery.Coordinates.seventhArea
import com.mandarinkafe.mandarin.delivery.Coordinates.sixthArea
import com.mandarinkafe.mandarin.delivery.Coordinates.tenthArea
import com.mandarinkafe.mandarin.delivery.Coordinates.thirdArea
import com.mandarinkafe.mandarin.delivery.Coordinates.twelfthArea
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

@Composable
fun LocationPicker() {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<MapView?>(null) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            getUserLocation(fusedLocationProviderClient, mapView.value)
        } else {
            moveCamera(mapView.value, 55.998040, 38.375328) // Подвинуть карту к Мандарину
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        Modifier
            .heightIn(min = Dimens.MapMinSize200, max = Dimens.MapMaxSize600)
            .widthIn(min = Dimens.MapMinSize200, max = Dimens.MapMaxSize600)
            .padding(Dimens.MarginBig20)
            .clip(RoundedCornerShape(Dimens.CornerRadius16))
    ) { _ ->
        AndroidView(
            factory = { MapView(it) },
            modifier = Modifier.fillMaxSize()
        ) { mapView.value = it }
    }

    LaunchedEffect(key1 = "loadMapView") {
        snapshotFlow { mapView.value }.collect { mapViewInstance ->
            mapViewInstance?.let {
                MapKitFactory.initialize(context)
                MapKitFactory.getInstance().onStart()
                it.onStart()
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    LaunchedEffect(key1 = "addColoredArea") {
        snapshotFlow { mapView.value }.collect { mapViewInstance ->
            mapViewInstance?.let {
                addColoredArea(
                    it, firstArea,
                    ContextCompat.getColor(context, R.color.first_area)
                )
                addAreaWithHole(
                    it, secondArea, firstArea,
                    ContextCompat.getColor(context, R.color.second_area)
                )
                addAreaWithHole(
                    it, thirdArea, secondArea,
                    ContextCompat.getColor(context, R.color.third_area)
                )
                addAreaWithHole(
                    it, fourthArea, thirdArea,
                    ContextCompat.getColor(context, R.color.fourth_area)
                )
                addAreaWithHole(
                    it, fifthArea, fourthArea,
                    ContextCompat.getColor(context, R.color.fifth_area)
                )
                addAreaWithHole(
                    it, sixthArea, fifthArea,
                    ContextCompat.getColor(context, R.color.sixth_area)
                )
                addAreaWithHole(
                    it, seventhArea, sixthArea,
                    ContextCompat.getColor(context, R.color.seventh_area)
                )
                addAreaWithHole(
                    it, eighthArea, seventhArea,
                    ContextCompat.getColor(context, R.color.eighth_area)
                )
                addAreaWithHole(
                    it, ninthArea, eighthArea,
                    ContextCompat.getColor(context, R.color.ninth_area)
                )
                addAreaWithHole(
                    it, tenthArea, ninthArea,
                    ContextCompat.getColor(context, R.color.tenth_area)
                )
                addAreaWithHole(
                    it, eleventhArea, tenthArea,
                    ContextCompat.getColor(context, R.color.eleventh_area)
                )
                addAreaWithHole(
                    it, twelfthArea, eleventhArea,
                    ContextCompat.getColor(context, R.color.twelfth_area)
                )
            }
        }
    }
}

private fun getUserLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    mapView: MapView?
) {
    if (ActivityCompat.checkSelfPermission(
            mapView?.context ?: return,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    // Запрос последнего известного местоположения
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            moveCamera(mapView, location.latitude, location.longitude)
        } else {
            requestLocationUpdates(fusedLocationProviderClient, mapView)
        }
    }.addOnFailureListener {
        requestLocationUpdates(fusedLocationProviderClient, mapView)
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun requestLocationUpdates(
    fusedLocationProviderClient: FusedLocationProviderClient,
    mapView: MapView?
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            Log.d("LocationPicker", "Updated location: ${location.latitude}, ${location.longitude}")
            moveCamera(mapView, location.latitude, location.longitude)
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    fusedLocationProviderClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

private fun moveCamera(mapView: MapView?, lat: Double, lon: Double) {
    val userLocation = Point(lat, lon)
    mapView?.mapWindow?.map?.move(
        CameraPosition(userLocation, 17.0f, 150.0f, 30.0f),
        Animation(Animation.Type.SMOOTH, 2f),
        null
    )
}

fun addColoredArea(mapView: MapView, area: List<Point>, color: Int) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    val polygon = Polygon(LinearRing(area), emptyList())
    val polygonObject = mapObjects.addPolygon(polygon)
    polygonObject.fillColor = color
    polygonObject.strokeColor = Color.TRANSPARENT
}

fun addAreaWithHole(mapView: MapView, outer: List<Point>, hole: List<Point>, color: Int) {
    val mapObjects = mapView.mapWindow?.map?.mapObjects ?: return
    val polygon = Polygon(LinearRing(outer), listOf(LinearRing(hole)))
    val polygonObject = mapObjects.addPolygon(polygon)
    polygonObject.fillColor = color
    polygonObject.strokeColor = Color.TRANSPARENT
}