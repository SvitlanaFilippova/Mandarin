package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun ClickToCopyText(
    text: String,
    showHint: Boolean = true,
)


