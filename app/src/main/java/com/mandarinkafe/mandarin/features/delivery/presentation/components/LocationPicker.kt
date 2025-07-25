@file:Suppress("MagicNumber")

package com.mandarinkafe.mandarin.features.delivery.presentation.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.MapAreas
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView

@Composable
fun LocationPicker() {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf<MapView?>(null) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && isLocationEnabled) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            getUserLocation(fusedLocationProviderClient, mapView.value)
        } else {
            moveCamera(mapView.value, 55.998040, 38.375328) // Подвинуть карту к Мандарину
        }
    }
    val placeMarks = remember { mutableListOf<PlacemarkMapObject>() }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        Modifier
            .heightIn(min = Dimens.MapMinSize200, max = Dimens.MapMaxSize600)
            .widthIn(min = Dimens.MapMinSize200, max = Dimens.MapMaxSize600)
            .padding(Dimens.MarginBig20)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
    ) { _ ->
        AndroidView(
            factory = { MapView(it) },
            modifier = Modifier.fillMaxSize()
        ) { mapView.value = it }
    }

    LaunchedEffect(key1 = "loadMapView") {
        snapshotFlow { mapView.value }.collect { mapViewInstance ->
            mapViewInstance?.let {
                MapKitFactory.getInstance().onStart()
                it.onStart()
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                it.mapWindow?.map?.addCameraListener(object : CameraListener {
                    override fun onCameraPositionChanged(
                        p0: Map,
                        cameraPosition: CameraPosition,
                        reason: CameraUpdateReason,
                        finished: Boolean
                    ) {
                        val zoomLevel = cameraPosition.zoom
                        if (zoomLevel > 12.0f) {
                            placeMarks.forEach {
                                it.isVisible = true
                            }
                        } else {
                            placeMarks.forEach {
                                it.isVisible = false
                            }
                        }
                    }
                })
            }
        }
    }
    mapView.value?.let {
        MapAreas(it)
        placeMarks.addAll(mapPlaceMarks(it))
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
        CameraPosition(userLocation, 17.0f, 0.0f, 30.0f),
        Animation(Animation.Type.SMOOTH, 2f),
        null
    )
}

