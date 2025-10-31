package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.runtime.Composable

@Composable
expect fun RequestLocationPermission(onGranted: () -> Unit)