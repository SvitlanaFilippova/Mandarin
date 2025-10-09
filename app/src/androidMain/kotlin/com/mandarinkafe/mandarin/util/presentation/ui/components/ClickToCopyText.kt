package com.mandarinkafe.mandarin.util.presentation.ui.components

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.TextStyle
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import kotlinx.coroutines.launch

@Composable
fun ClickToCopyText(
    text: String,
    showHint: Boolean = true,
    style: TextStyle,
    color: Color,

    ) {
    val clipboard = LocalClipboard.current
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Text(
        text = text,
        style = style,
        color = color,
        modifier = Modifier.clickable {
            scope.launch {
                val clipData = ClipData.newPlainText(text, text)
                val clipEntry = clipData.toClipEntry()
                clipboard.setClipEntry(clipEntry)
                if (showHint) {
                    snackbarHostState.showSnackbar("Скопировано $text")
                }
            }
        }
    )
}
