package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun MakeCall(
    phoneNumber: String,
    onFail: () -> Unit
) {
    val context = LocalContext.current
    try {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onFail()
    }
}

