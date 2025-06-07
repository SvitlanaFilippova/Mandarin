package com.mandarinkafe.mandarin.splash.presentation.model

import androidx.compose.ui.Alignment

data class SplashElement(
    val resId: Int,
    val alignment: Alignment,
    val targetOffsetX: Float,
    val targetOffsetY: Float,
    val offsetX: Float,
    val offsetY: Float
)