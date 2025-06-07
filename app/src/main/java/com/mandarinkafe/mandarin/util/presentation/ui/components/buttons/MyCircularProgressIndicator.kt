package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@Composable
fun MyCircularProgressIndicator(
    modifier: Modifier = Modifier,
    strokeWidth: Dp,
) {
    CircularProgressIndicator(
        modifier = modifier,
        strokeWidth = strokeWidth,
        color = Colors.LightGrey,
        trackColor = Colors.DarkGrey
    )
}