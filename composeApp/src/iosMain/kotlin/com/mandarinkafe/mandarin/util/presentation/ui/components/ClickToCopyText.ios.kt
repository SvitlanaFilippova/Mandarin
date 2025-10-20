package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import kotlinx.coroutines.launch
import platform.UIKit.UIPasteboard

@Composable
actual fun ClickToCopyText(
    text: String,
    showHint: Boolean,
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Text(
        text = text,
        style = TextStyle.Default,
        color = Color.Unspecified,
        modifier = Modifier.clickable {
            scope.launch {
                UIPasteboard.generalPasteboard.string = text
                if (showHint) {
                    snackbarHostState.showSnackbar("Скопировано $text")
                }
            }
        }
    )
}


