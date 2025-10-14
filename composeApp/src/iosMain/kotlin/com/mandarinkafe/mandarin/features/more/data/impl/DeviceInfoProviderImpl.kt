package com.mandarinkafe.mandarin.features.more.data.impl

import com.mandarinkafe.mandarin.features.more.data.DeviceInfoProvider
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class DeviceInfoProviderImpl : DeviceInfoProvider {
    override fun getDeviceInfo(): String {
        val device = UIDevice.currentDevice
        val bundle = NSBundle.mainBundle
        
        val versionName = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
        val versionCode = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "Unknown"
        
        val osVersion = device.systemVersion
        val deviceModel = device.model
        val deviceName = device.name
        
        return buildString {
            append("\uD83C\uDF10 OS: iOS $osVersion\n")
            append("\uD83D\uDCF1 Device: $deviceModel ($deviceName)\n")
            append("\uD83D\uDD04 App version: $versionName ($versionCode)\n")
        }
    }
}

