package com.mandarinkafe.mandarin.splash.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import dev.icerock.moko.resources.ImageResource

@Immutable
data class SplashElement(
    val resId: ImageResource,
    val alignment: Alignment,
    val targetOffsetX: Float,
    val targetOffsetY: Float,
    val offsetX: Float,
    val offsetY: Float,
)