package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import com.mandarinkafe.mandarin.features.map.MapCameraController
import com.mandarinkafe.mandarin.features.map.MapCameraControllerImpl
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun createMapCameraController(): MapCameraController {
    return MapCameraControllerImpl(null)
}