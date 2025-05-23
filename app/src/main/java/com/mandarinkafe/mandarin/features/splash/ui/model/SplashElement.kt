package com.mandarinkafe.mandarin.features.splash.ui.model

import androidx.compose.ui.Alignment

data class SplashElement(
    val resId: Int,
    val alignment: Alignment,
    val offsetX: Float = 0f,      // стартовая позиция
    val offsetY: Float = 0f,
    val targetOffsetX: Float = 0f, // конечная позиция
    val targetOffsetY: Float = 0f
)