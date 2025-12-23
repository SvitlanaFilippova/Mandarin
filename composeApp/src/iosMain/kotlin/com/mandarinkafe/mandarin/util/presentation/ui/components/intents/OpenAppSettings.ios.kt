package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openAppSettings(context: Any?) {
    val settingsUrlString = "app-settings:"
    val settingsUrl = NSURL.URLWithString(settingsUrlString)
    settingsUrl?.let {
        UIApplication.sharedApplication.openURL(it, mapOf<Any?, Any>()) { }
    }
}

