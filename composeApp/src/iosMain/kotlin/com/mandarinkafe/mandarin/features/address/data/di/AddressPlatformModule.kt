package com.mandarinkafe.mandarin.features.address.data.di

import com.mandarinkafe.mandarin.features.address.data.impl.AddressRepositoryImpl
import com.mandarinkafe.mandarin.features.address.data.impl.FusedLocationRepositoryImpl
import com.mandarinkafe.mandarin.features.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.domain.api.FusedLocationRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val addressPlatformModule = module {
    // iOS-specific Repositories
    singleOf(::FusedLocationRepositoryImpl) { bind<FusedLocationRepository>() }
    single<AddressRepository> {
        AddressRepositoryImpl(
            coroutineScope = get(),
            networkMonitor = get()
        )
    }
}