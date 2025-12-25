package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import android.content.Context
import android.content.Intent
import android.provider.Settings

actual fun openAppSettings(context: Any?) {
    val androidContext = context as? Context ?: return
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", androidContext.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    androidContext.startActivity(intent)
}

