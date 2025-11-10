package com.mandarinkafe.mandarin.core.presentation

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.impl.AppLifecycleManager

/**
 * Expect функция для обработки lifecycle приложения
 * Реализуется на каждой платформе отдельно
 */
@Composable
expect fun AppLifecycleHandler(
    appLifecycleManager: AppLifecycleManager
)

