package com.mandarinkafe.mandarin.features.address.di

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mandarinkafe.mandarin.features.address.data.impl.AddressRepositoryImpl
import com.mandarinkafe.mandarin.features.address.data.impl.DeliveryAreaRepositoryImpl
import com.mandarinkafe.mandarin.features.address.data.impl.FusedLocationRepositoryImpl
import com.mandarinkafe.mandarin.features.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.domain.api.FusedLocationRepository
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val addressPlatformModule = module {

    // MapKit & Search
    single<MapKit> {
        MapKitFactory.initialize(androidContext())
        MapKitFactory.getInstance()
    }

    single<SearchManager> {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    // Провайдер FusedLocationProviderClient
    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }

    // Android-specific Repositories
    singleOf(::FusedLocationRepositoryImpl) { bind<FusedLocationRepository>() }
    singleOf(::AddressRepositoryImpl) { bind<AddressRepository>() }

}






