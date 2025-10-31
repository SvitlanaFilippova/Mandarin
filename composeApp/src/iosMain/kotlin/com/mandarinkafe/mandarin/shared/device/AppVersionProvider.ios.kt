package com.mandarinkafe.mandarin.shared.device

import platform.Foundation.NSBundle

actual class AppVersionProvider {
    actual fun getVersionName(): String {
        val bundle = NSBundle.mainBundle
        return bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
    }

    actual fun getVersionCode(): String {
        val bundle = NSBundle.mainBundle
        return bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "0"
    }
}


