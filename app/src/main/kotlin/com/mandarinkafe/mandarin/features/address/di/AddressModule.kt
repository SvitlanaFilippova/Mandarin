package com.mandarinkafe.mandarin.features.address.di

import org.koin.dsl.module

val addressModule = module {

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
//    // Fused Location
//    single<FusedLocationProviderClient> {
//        LocationServices.getFusedLocationProviderClient(androidContext())
//    }
//
//    singleOf(::FusedLocationRepositoryImpl) { bind<FusedLocationRepository>() }
//    singleOf(::GetCurrentLocationUseCaseImpl) { bind<GetCurrentLocationUseCase>() }
//
//    // Address Repository & Interactor
//    singleOf(::AddressRepositoryImpl) { bind<AddressRepository>() }
//    singleOf(::AddressSearchInteractorImpl) { bind<AddressSearchInteractor>() }
//
//    // Saved Addresses
////    singleOf(::AddressStorageImpl) { bind<AddressStorage>() }
//    singleOf(::SavedAddressRepositoryImpl) { bind<SavedAddressRepository>() }
//    singleOf(::SaveAddressUseCaseImpl) { bind<SaveAddressUseCase>() }
//    singleOf(::RemoveAddressUseCaseImpl) { bind<RemoveAddressUseCase>() }
//    singleOf(::GetSavedAddressesUseCaseImpl) { bind<GetSavedAddressesUseCase>() }
//
//    // Delivery Areas
//    singleOf(::DeliveryAreaRepositoryImpl) { bind<DeliveryAreaRepository>() }
//    singleOf(::GetDeliveryZoneUseCaseImpl) { bind<GetDeliveryZoneUseCase>() }
//
//    // ViewModel
//    viewModelOf(::AddressDetailsViewModel)
//    viewModelOf(::AddressViewModel)
//    viewModelOf(::SavedAddressesViewModel)

}
