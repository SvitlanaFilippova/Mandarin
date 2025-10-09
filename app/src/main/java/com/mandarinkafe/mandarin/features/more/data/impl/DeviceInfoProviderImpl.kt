package com.mandarinkafe.mandarin.features.more.data.impl

import android.content.Context
import android.os.Build
import com.mandarinkafe.mandarin.features.more.data.DeviceInfoProvider
import com.mandarinkafe.mandarin.util.AppLog

class DeviceInfoProviderImpl(private val context: Context) : DeviceInfoProvider {
    override fun getDeviceInfo(): String {
        val versionName = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "Unknown"
            AppLog.e("Unknown versionName, $e")
        }

        val versionCode = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                pInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            "Unknown"
            AppLog.e("Unknown versionCode, $e")
        }

        return buildString {
            append("\uD83C\uDF10 OS: Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})\n")
            append("\uD83D\uDCF1 Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
            append("\uD83D\uDD04 App version: $versionName ($versionCode)\n")
        }
    }
}