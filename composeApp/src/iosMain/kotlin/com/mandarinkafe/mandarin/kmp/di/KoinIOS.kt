package com.mandarinkafe.mandarin.kmp.di

import com.mandarinkafe.mandarin.features.address.data.di.addressPlatformModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructurePlatformModule
import com.mandarinkafe.mandarin.features.menu.di.menuPlatformModule
import com.mandarinkafe.mandarin.features.more.di.morePlatformModule


fun initKoinIOS() = initKoinCommon {
    modules(
        infrastructurePlatformModule,
        menuPlatformModule,
        addressPlatformModule,
        morePlatformModule,
    )
}
