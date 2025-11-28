package com.mandarinkafe.mandarin.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mandarinkafe.mandarin.core.domain.impl.AppLifecycleManager

/**
 * Обработчик lifecycle для Android
 * Использует LocalLifecycleOwner для отслеживания возврата приложения из фона
 */
@Composable
actual fun AppLifecycleHandler(
    appLifecycleManager: AppLifecycleManager
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                appLifecycleManager.onAppForegrounded()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

