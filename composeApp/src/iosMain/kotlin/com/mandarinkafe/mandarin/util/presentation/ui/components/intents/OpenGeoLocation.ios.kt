package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.UIKit.UIApplication

@Composable
actual fun OpenGeoLocation(address: String) {
    // Кодируем адрес для URL
    val encodedAddress = address.replace(" ", "+")
    
    // Используем Apple Maps URL scheme
    val urlString = "http://maps.apple.com/?q=$encodedAddress"
    val url = NSURL.URLWithString(urlString)
    
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url, mapOf<Any?, Any>()) { success ->
            // Можно добавить логирование если нужно
        }
    }
}

