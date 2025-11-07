package com.mandarinkafe.mandarin.shared.device

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

actual class DeviceInfoProvider {
    actual fun getDeviceInfo(): String {
        val device = UIDevice.currentDevice
        val bundle = NSBundle.mainBundle

        val appVersion =
            bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
        val buildNumber =
            bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "Unknown"

        return "iOS ${device.systemVersion}, " +
                "App v$appVersion ($buildNumber), " +
                "Device: ${device.model} ${device.name}"
    }

    actual fun getDeviceName(): String {
        val device = UIDevice.currentDevice
        // device.model returns "iPhone" or "iPad"
        // device.name returns user's device name like "John's iPhone"
        // We'll use model for a generic name
        return device.model as? String ?: "iOS Device"
    }
}





