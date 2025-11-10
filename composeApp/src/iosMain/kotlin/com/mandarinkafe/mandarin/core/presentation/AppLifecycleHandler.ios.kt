package com.mandarinkafe.mandarin.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.mandarinkafe.mandarin.core.domain.impl.AppLifecycleManager
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification

/**
 * Обработчик lifecycle для iOS
 * Использует NSNotificationCenter для отслеживания возврата приложения из фона
 */
@Composable
actual fun AppLifecycleHandler(
    appLifecycleManager: AppLifecycleManager
) {
    DisposableEffect(appLifecycleManager) {
        val notificationCenter = NSNotificationCenter.defaultCenter

        val observer = notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = null
        ) { _: NSNotification? ->
            appLifecycleManager.onAppForegrounded()
        }

        onDispose {
            notificationCenter.removeObserver(observer)
        }
    }
}

