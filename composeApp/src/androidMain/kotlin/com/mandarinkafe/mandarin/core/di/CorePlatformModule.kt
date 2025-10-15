package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.shared.device.DeviceInfoProvider
import com.mandarinkafe.mandarin.shared.device.DeviceInfoProviderImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val corePlatformModule = module {
    // NetworkMonitor (требует Context на Android)
    single { NetworkMonitor(get()) }
    
    // DeviceInfoProvider (Android-specific)
    singleOf(::DeviceInfoProviderImpl) { bind<DeviceInfoProvider>() }
}

