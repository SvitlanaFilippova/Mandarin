package com.mandarinkafe.mandarin.util

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Modifier.bottomSheetContentModifier(): Modifier {
    return this.fillMaxHeight()
}

