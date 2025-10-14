package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import org.koin.dsl.module

val corePlatformModule = module {
    // NetworkMonitor (для iOS не требует параметров)
    single { NetworkMonitor() }
}

