package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton

@Composable
fun DeliveryScreen(onBackClick: () -> Boolean) {
    ScreenTitleWithBackButton(
        name = "DeliveryScreen",
        onBackClick = { onBackClick() },
    )
}