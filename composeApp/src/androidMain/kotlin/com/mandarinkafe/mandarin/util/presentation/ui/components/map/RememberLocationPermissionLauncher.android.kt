package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
actual fun rememberLocationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): LocationPermissionLauncher {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            onDenied()
        }
    }

    return remember(launcher, context) {
        LocationPermissionLauncher(
            requestPermission = {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            hasPermission = {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            },
            canRequestPermission = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    true
                } else {
                    // Если разрешения нет, проверяем, можем ли мы показать диалог запроса
                    // Если shouldShowRequestPermissionRationale возвращает false и разрешения нет,
                    // значит пользователь выбрал "Don't ask again" и нужно открыть настройки
                    val activity = context as? Activity
                    activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } ?: true // Если не можем определить, пробуем запросить
                }
            }
        )
    }
}

