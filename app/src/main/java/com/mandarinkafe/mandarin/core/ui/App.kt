package com.mandarinkafe.mandarin.core.ui
import android.app.Application
import com.mandarinkafe.mandarin.di.dataModule
import com.mandarinkafe.mandarin.di.domainModule
import com.mandarinkafe.mandarin.di.uiModule
import com.mandarinkafe.mandarin.di.viewModelModule

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(dataModule, viewModelModule, domainModule, uiModule))
        }

    }

}