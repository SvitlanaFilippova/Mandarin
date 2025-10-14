package com.mandarinkafe.mandarin.features.address.di

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mandarinkafe.mandarin.features.address.address.data.impl.AddressRepositoryImpl
import com.mandarinkafe.mandarin.features.address.address.data.impl.DeliveryAreaRepositoryImpl
import com.mandarinkafe.mandarin.features.address.address.data.impl.FusedLocationRepositoryImpl
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.impl.AddressSearchInteractorImpl
import com.mandarinkafe.mandarin.features.address.address.domain.impl.GetCurrentLocationUseCaseImpl
import com.mandarinkafe.mandarin.features.address.address.domain.impl.GetDeliveryZoneUseCaseImpl
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.savedadresses.data.impl.SavedAddressRepositoryImpl
import com.mandarinkafe.mandarin.features.savedadresses.data.sharedprefs.AddressStorage
import com.mandarinkafe.mandarin.features.savedadresses.data.sharedprefs.AddressStorageImpl
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SaveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository
import com.mandarinkafe.mandarin.features.savedadresses.domain.impl.GetSavedAddressesUseCaseImpl
import com.mandarinkafe.mandarin.features.savedadresses.domain.impl.RemoveAddressUseCaseImpl
import com.mandarinkafe.mandarin.features.savedadresses.domain.impl.SaveAddressUseCaseImpl
import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesViewModel
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addressModule = module {

    // MapKit & Search
    single<MapKit> {
        MapKitFactory.initialize(androidContext())
        MapKitFactory.getInstance()
    }

    single<SearchManager> {
        val mapKit: MapKit = get() // заставляем Koin проинициализировать MapKit
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    // Fused Location
    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }

    singleOf(::FusedLocationRepositoryImpl) { bind<FusedLocationRepository>() }
    singleOf(::GetCurrentLocationUseCaseImpl) { bind<GetCurrentLocationUseCase>() }

    // Address Repository & Interactor
    singleOf(::AddressRepositoryImpl) { bind<AddressRepository>() }
    singleOf(::AddressSearchInteractorImpl) { bind<AddressSearchInteractor>() }

    // Saved Addresses
    singleOf(::AddressStorageImpl) { bind<AddressStorage>() }
    singleOf(::SavedAddressRepositoryImpl) { bind<SavedAddressRepository>() }
    singleOf(::SaveAddressUseCaseImpl) { bind<SaveAddressUseCase>() }
    singleOf(::RemoveAddressUseCaseImpl) { bind<RemoveAddressUseCase>() }
    singleOf(::GetSavedAddressesUseCaseImpl) { bind<GetSavedAddressesUseCase>() }

    // Delivery Areas
    singleOf(::DeliveryAreaRepositoryImpl) { bind<DeliveryAreaRepository>() }
    singleOf(::GetDeliveryZoneUseCaseImpl) { bind<GetDeliveryZoneUseCase>() }

    // ViewModel
    viewModelOf(::AddressDetailsViewModel)
    viewModelOf(::AddressViewModel)
    viewModelOf(::SavedAddressesViewModel)

}
