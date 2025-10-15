package com.mandarinkafe.mandarin.features.address.di

import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.address.data.impl.AddressRepositoryImpl
import com.mandarinkafe.mandarin.features.address.data.impl.DeliveryAreaRepositoryImpl
import com.mandarinkafe.mandarin.features.address.data.impl.FusedLocationRepositoryImpl
import com.mandarinkafe.mandarin.features.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesViewModel
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.runtime.search.SearchFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
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

    // Android-specific Repositories
    singleOf(::FusedLocationRepositoryImpl) { bind<FusedLocationRepository>() }
    singleOf(::AddressRepositoryImpl) { bind<AddressRepository>() }
    singleOf(::DeliveryAreaRepositoryImpl) { bind<DeliveryAreaRepository>() }

    // ViewModels
    viewModelOf(::AddressDetailsViewModel)
    viewModelOf(::AddressViewModel)
    viewModelOf(::SavedAddressesViewModel)
}

