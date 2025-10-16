package com.mandarinkafe.mandarin.shared.device

import platform.UIKit.UIDevice
import platform.Foundation.NSBundle

actual class DeviceInfoProvider {
    actual fun getDeviceInfo(): String {
        val device = UIDevice.currentDevice
        val bundle = NSBundle.mainBundle
        
        val appVersion = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
        val buildNumber = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "Unknown"
        
        return "iOS ${device.systemVersion}, " +
                "App v$appVersion ($buildNumber), " +
                "Device: ${device.model} ${device.name}"
    }
}





