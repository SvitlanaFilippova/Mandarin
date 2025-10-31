package com.mandarinkafe.mandarin.kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.kmp.di.initKoinIOS
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi

@Suppress("FunctionNaming")
@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController {
    // Инициализируем Koin
    initKoinIOS()

    // Инициализируем Napier
    Napier.base(DebugAntilog())


    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .background(Colors.AppBlack)
            .windowInsetsPadding(
                WindowInsets.safeContent.only(
                    WindowInsetsSides.Top
                )
            )
    ) {
        MainScreen()
    }
}