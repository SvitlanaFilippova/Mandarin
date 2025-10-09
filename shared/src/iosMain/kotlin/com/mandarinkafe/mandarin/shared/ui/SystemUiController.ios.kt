package com.mandarinkafe.mandarin.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * iOS реализация SystemUiController
 * В iOS управление системным UI происходит через UIKit
 */
actual class SystemUiController {
    actual fun setStatusBarColor(color: Long, darkIcons: Boolean) {
        // В iOS цвет статус-бара обычно управляется через:
        // 1. Info.plist (UIStatusBarStyle)
        // 2. UIApplication.setStatusBarStyle()
        // 3. SwiftUI .preferredColorScheme()

        // Для KMP можно использовать platform-specific код:
        // platform.Foundation.NSUserDefaults.standardUserDefaults
        //     .setObject(if (darkIcons) "UIStatusBarStyleDarkContent" else "UIStatusBarStyleLightContent", 
        //                "UIStatusBarStyle")

        // Пока что заглушка - в реальном проекте здесь будет вызов iOS API
        println("iOS: Setting status bar color to ${Color(color)} with darkIcons=$darkIcons")
    }

    actual fun setNavigationBarColor(color: Long, darkIcons: Boolean) {
        // В iOS нет навигационной панели как в Android
        // Но можно управлять цветом tab bar или других элементов

        // Пока что заглушка
        println("iOS: Setting navigation bar color to ${Color(color)} with darkIcons=$darkIcons")
    }
}

/**
 * iOS реализация rememberSystemUiController
 */
@Composable
actual fun rememberSystemUiController(): SystemUiController {
    return remember { SystemUiController() }
}
