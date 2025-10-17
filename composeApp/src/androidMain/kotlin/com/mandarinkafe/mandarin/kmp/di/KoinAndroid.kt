package com.mandarinkafe.mandarin.kmp.di

import android.app.Application
import com.mandarinkafe.mandarin.features.address.di.addressPlatformModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructurePlatformModule
import com.mandarinkafe.mandarin.features.menu.di.menuPlatformModule
import com.mandarinkafe.mandarin.features.more.di.morePlatformModule
import org.koin.android.ext.koin.androidContext

fun initKoinAndroid(app: Application) = initKoinCommon {
    androidContext(app)
    modules(
        infrastructurePlatformModule,
        menuPlatformModule,
        addressPlatformModule,
        morePlatformModule,
    )
}