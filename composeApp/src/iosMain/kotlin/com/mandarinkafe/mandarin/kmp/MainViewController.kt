package com.mandarinkafe.mandarin.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.mandarinkafe.mandarin.kmp.di.initKoinIOS
import moe.tlaster.precompose.PreComposeApp

fun MainViewController() = ComposeUIViewController {
    // Инициализируем Koin
    initKoinIOS()
    
    PreComposeApp {
        MainScreen()
    }
}