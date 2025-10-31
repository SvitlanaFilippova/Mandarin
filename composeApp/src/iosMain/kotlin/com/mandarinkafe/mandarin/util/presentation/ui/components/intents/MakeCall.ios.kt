package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun MakeCall(phoneNumber: String, onFail: () -> Unit) {
    try {
        // Очищаем номер от пробелов, скобок и других символов для tel: URL scheme
        val cleanPhoneNumber = phoneNumber
            .replace(Regex("[\\s()–—-]"), "") // удаляем пробелы, скобки, дефисы и тире
            .replace("\u00A0", "") // удаляем неразрывные пробелы

        val telUrl = NSURL.URLWithString("tel:$cleanPhoneNumber")

        if (telUrl != null && UIApplication.sharedApplication.canOpenURL(telUrl)) {
            UIApplication.sharedApplication.openURL(telUrl, mapOf<Any?, Any>()) { success ->
                if (!success) {
                    onFail()
                }
            }
        } else {
            onFail()
        }
    } catch (e: Exception) {
        onFail()
    }
}

