package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): LocationPermissionLauncher {
    val locationManager = remember { CLLocationManager() }
    var authorizationStatus by remember {
        mutableStateOf(CLLocationManager.authorizationStatus())
    }

    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                authorizationStatus = CLLocationManager.authorizationStatus()
                when (authorizationStatus) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> {
                        onGranted()
                    }
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> {
                        onDenied()
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        locationManager.delegate = delegate
        onDispose {
            locationManager.delegate = null
        }
    }

    return remember(locationManager, delegate) {
        LocationPermissionLauncher(
            requestPermission = {
                val currentStatus = CLLocationManager.authorizationStatus()
                authorizationStatus = currentStatus
                when (currentStatus) {
                    kCLAuthorizationStatusNotDetermined -> {
                        locationManager.requestWhenInUseAuthorization()
                    }
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> {
                        onGranted()
                    }
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> {
                        onDenied()
                    }
                }
            },
            hasPermission = {
                val currentStatus = CLLocationManager.authorizationStatus()
                authorizationStatus = currentStatus
                currentStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                        currentStatus == kCLAuthorizationStatusAuthorizedAlways
            },
            canRequestPermission = {
                val currentStatus = CLLocationManager.authorizationStatus()
                authorizationStatus = currentStatus
                // На iOS можем запросить разрешение только если статус NotDetermined
                // Если Denied или Restricted - нужно открыть настройки
                currentStatus == kCLAuthorizationStatusNotDetermined
            }
        )
    }
}

