package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun MakeCall(phoneNumber: String, onFail: () -> Unit) {
    try {
        val telUrl = NSURL.URLWithString("tel:$phoneNumber")
        if (telUrl != null && UIApplication.sharedApplication.canOpenURL(telUrl)) {
            UIApplication.sharedApplication.openURL(telUrl)
        } else {
            onFail()
        }
    } catch (e: Exception) {
        onFail()
    }
}

