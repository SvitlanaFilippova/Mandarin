package com.mandarinkafe.mandarin.features.splash.ui.model

import androidx.compose.ui.Alignment

data class SplashElement(
    val resId: Int,
    val alignment: Alignment,
    val targetOffsetX: Float,
    val targetOffsetY: Float,
    val offsetX: Float,
    val offsetY: Float
)