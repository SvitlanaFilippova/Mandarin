package com.mandarinkafe.mandarin.core.ui

import android.app.Application
import com.mandarinkafe.mandarin.BuildConfig
import com.yandex.mapkit.MapKitFactory

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}