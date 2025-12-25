package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun RequestLocationPermission(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var callbackInvoked by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!callbackInvoked) {
            callbackInvoked = true
            if (isGranted) {
                onGranted()
            } else {
                onDenied()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!callbackInvoked) {
            if (!hasPermission) {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                callbackInvoked = true
                onGranted()
            }
        }
    }
}