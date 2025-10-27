package com.mandarinkafe.mandarin.shared.device

import android.content.Context
import android.os.Build
import io.github.aakira.napier.Napier

actual class AppVersionProvider(private val context: Context) {
    actual fun getVersionName(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
                ?: "Unknown"
        } catch (e: Exception) {
            Napier.e("Unknown versionName, $e")
            "Unknown"
        }
    }
    
    actual fun getVersionCode(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }
            versionCode.toString()
        } catch (e: Exception) {
            Napier.e("Unknown versionCode, $e")
            "0"
        }
    }
}


