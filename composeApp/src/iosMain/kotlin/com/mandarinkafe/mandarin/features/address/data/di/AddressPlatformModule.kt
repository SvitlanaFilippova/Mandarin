package com.mandarinkafe.mandarin.features.address.data.di

import org.koin.dsl.module

val addressPlatformModule = module {
    // TODO (требует полной реализации)

    //  Образец из Android:

//    // MapKit & Search
//    single<MapKit> {
//        MapKitFactory.initialize(androidContext())
//        MapKitFactory.getInstance()
//    }
//
//    single<SearchManager> {
//        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
//    }
//
//    // Провайдер FusedLocationProviderClient
//    single<FusedLocationProviderClient> {
//        LocationServices.getFusedLocationProviderClient(androidContext())
//    }
//
//    // Android-specific Repositories
//    singleOf(::FusedLocationRepositoryImpl) { bind<FusedLocationRepository>() }
//    singleOf(::AddressRepositoryImpl) { bind<AddressRepository>() }
//    singleOf(::DeliveryAreaRepositoryImpl) { bind<DeliveryAreaRepository>() }

}