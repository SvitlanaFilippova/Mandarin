package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun OpenGeoLocation(address: String) {
    val context = LocalContext.current
    val intent = Intent(
        Intent.ACTION_VIEW,
        "geo:0,0?q=$address".toUri()
    )
    context.startActivity(intent)
}

