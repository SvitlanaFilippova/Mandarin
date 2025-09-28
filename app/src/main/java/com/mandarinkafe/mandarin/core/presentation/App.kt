package com.mandarinkafe.mandarin.core.presentation

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.MenuRefreshOnResumeObserver
import com.mandarinkafe.mandarin.util.Constants.LOCALE_RU
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var menuRefreshObserver: MenuRefreshOnResumeObserver

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.setLocale(LOCALE_RU)
        ProcessLifecycleOwner.get().lifecycle.addObserver(menuRefreshObserver)
    }
}