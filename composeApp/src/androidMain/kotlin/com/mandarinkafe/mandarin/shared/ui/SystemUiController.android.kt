package com.mandarinkafe.mandarin.shared.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

/**
 * Android реализация SystemUiController
 */
actual class SystemUiController(
    private val activity: ComponentActivity
) {
    actual fun setStatusBarColor(color: Long, darkIcons: Boolean) {
        activity.window.statusBarColor = Color(color).toArgb()

        // Управление темными иконками через системные флаги
        val flags = activity.window.decorView.systemUiVisibility
        if (darkIcons) {
            activity.window.decorView.systemUiVisibility =
                flags or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            activity.window.decorView.systemUiVisibility =
                flags and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    actual fun setNavigationBarColor(color: Long, darkIcons: Boolean) {
        activity.window.navigationBarColor = Color(color).toArgb()

        // Управление темными иконками навигационной панели
        val flags = activity.window.decorView.systemUiVisibility
        if (darkIcons) {
            activity.window.decorView.systemUiVisibility =
                flags or android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            activity.window.decorView.systemUiVisibility =
                flags and android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
}

/**
 * Android реализация rememberSystemUiController
 */
@Composable
actual fun rememberSystemUiController(): SystemUiController {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    return remember { SystemUiController(activity) }
}