package com.mandarinkafe.mandarin.shared.device

expect class DeviceInfoProvider {
    fun getDeviceInfo(): String
    fun getDeviceName(): String
    fun getPlatform(): String
}
