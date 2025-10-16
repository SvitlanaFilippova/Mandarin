package com.mandarinkafe.mandarin.features.more.di

import com.mandarinkafe.mandarin.shared.device.DeviceInfoProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val morePlatformModule = module {
    // DeviceInfoProvider
    singleOf(::DeviceInfoProvider)
}


