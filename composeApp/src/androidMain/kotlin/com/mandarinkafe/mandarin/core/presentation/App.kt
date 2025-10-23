package com.mandarinkafe.mandarin.core.presentation

import android.app.Application
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.kmp.di.initKoinAndroid
import com.mandarinkafe.mandarin.util.Constants.LOCALE_RU
import com.yandex.mapkit.MapKitFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация Napier (логгер)
        Napier.base(DebugAntilog())

        // Настройка MapKit
        MapKitFactory.setApiKey(BuildKonfig.MAPKIT_API_KEY)
        MapKitFactory.setLocale(LOCALE_RU)

        // Инициализация Koin
        initKoinAndroid(this)
    }
}
