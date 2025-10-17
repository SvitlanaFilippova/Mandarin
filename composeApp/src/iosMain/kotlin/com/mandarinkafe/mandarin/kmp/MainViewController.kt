package com.mandarinkafe.mandarin.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.mandarinkafe.mandarin.kmp.di.initKoinIOS
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import moe.tlaster.precompose.PreComposeApp

fun MainViewController() = ComposeUIViewController {
    // Инициализируем Koin
    initKoinIOS()

    // Инициализируем Napier
    Napier.base(DebugAntilog())

    // TODO добавить инициализацию Mapkit SDK

    PreComposeApp {
        MainScreen()
    }
}