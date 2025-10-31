package com.mandarinkafe.mandarin.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun MandarinTheme(
    content: @Composable () -> Unit
) {
    val colorScheme =
        darkColorScheme(
            primary = Colors.Orange,
            background = Colors.AppBlack,
            onPrimary = Colors.White
        )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
