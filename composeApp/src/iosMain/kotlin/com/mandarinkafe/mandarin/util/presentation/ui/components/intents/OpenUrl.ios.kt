package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import androidx.compose.runtime.Composable
import io.github.aakira.napier.Napier
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun OpenUrl(url: String, onFail: () -> Unit) {
    try {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null && UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            UIApplication.sharedApplication.openURL(nsUrl, mapOf<Any?, Any>()) { success ->
                if (!success) {
                    onFail()
                }
            }
        } else {
            onFail()
        }
    } catch (e: Exception) {
        onFail()
        Napier.e("OpenUrl error: $e")
    }
}