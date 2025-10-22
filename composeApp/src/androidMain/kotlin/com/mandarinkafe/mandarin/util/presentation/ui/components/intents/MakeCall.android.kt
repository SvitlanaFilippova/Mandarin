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
        // Очищаем номер от пробелов, скобок и других символов для tel: URL scheme
        val cleanPhoneNumber = phoneNumber
            .replace(Regex("[\\s()–—-]"), "") // удаляем пробелы, скобки, дефисы и тире
            .replace("\u00A0", "") // удаляем неразрывные пробелы
        
        val intent = Intent(Intent.ACTION_DIAL, "tel:$cleanPhoneNumber".toUri())
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onFail()
    }
}

