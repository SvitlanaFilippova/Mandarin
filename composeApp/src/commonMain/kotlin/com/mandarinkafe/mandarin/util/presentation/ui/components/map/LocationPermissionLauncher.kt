package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.runtime.Composable

/**
 * Возвращает функции для работы с разрешением на геолокацию:
 * - requestPermission: функция для запроса разрешения
 * - hasPermission: функция для проверки наличия разрешения
 * - canRequestPermission: функция для проверки, можем ли мы запросить разрешение (true) или нужно открыть настройки (false)
 */
data class LocationPermissionLauncher(
    val requestPermission: () -> Unit,
    val hasPermission: () -> Boolean,
    val canRequestPermission: () -> Boolean,
)

@Composable
expect fun rememberLocationPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): LocationPermissionLauncher

