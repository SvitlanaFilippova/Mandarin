package com.mandarinkafe.mandarin.shared.device

import android.content.Context
import android.os.Build
import io.github.aakira.napier.Napier

actual class DeviceInfoProvider(private val context: Context) {
    actual fun getDeviceInfo(): String {
        val versionName = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "Unknown"
            Napier.e("Unknown versionName, $e")
        }

        val versionCode = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            Napier.e("Unknown versionCode, $e")
            0L
        }

        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}), " +
                "App v$versionName ($versionCode), " +
                "Device: ${Build.MANUFACTURER} ${Build.MODEL}"
    }
}




