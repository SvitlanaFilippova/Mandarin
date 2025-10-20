package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
expect fun ClickToCopyText(
    text: String,
    showHint: Boolean = true,
    style: TextStyle,
    color: Color,
)


