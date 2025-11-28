package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import io.github.aakira.napier.Napier

@Composable
actual fun OpenUrl(
    url: String,
    onFail: () -> Unit,
) {
    val context = LocalContext.current
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onFail()
        Napier.e("OpenUrl error: $e")
    }
}
