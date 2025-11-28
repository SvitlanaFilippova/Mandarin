package com.mandarinkafe.mandarin.kmp.di

import com.mandarinkafe.mandarin.core.di.corePlatformModule
import com.mandarinkafe.mandarin.features.address.data.di.addressPlatformModule
import com.mandarinkafe.mandarin.features.infrastructure.di.infrastructurePlatformModule
import com.mandarinkafe.mandarin.features.menu.di.menuPlatformModule
import com.mandarinkafe.mandarin.features.more.di.morePlatformModule
import com.mandarinkafe.mandarin.features.payment.di.paymentPlatformModule

fun initKoinIOS() = initKoinCommon {
    modules(
        corePlatformModule,
        infrastructurePlatformModule,
        menuPlatformModule,
        addressPlatformModule,
        morePlatformModule,
        paymentPlatformModule,
    )
}