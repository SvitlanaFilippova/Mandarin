package com.mandarinkafe.mandarin.features.address.di

import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.address.data.impl.DeliveryAreaRepositoryImpl
import com.mandarinkafe.mandarin.features.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.domain.impl.AddressSearchInteractorImpl
import com.mandarinkafe.mandarin.features.address.domain.impl.GetCurrentLocationUseCaseImpl
import com.mandarinkafe.mandarin.features.address.domain.impl.GetDeliveryZoneUseCaseImpl
import com.mandarinkafe.mandarin.core.di.DiConstants
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.features.savedadresses.data.impl.SavedAddressRepositoryImpl
import com.mandarinkafe.mandarin.features.savedadresses.data.network.AddressServerApi
import com.mandarinkafe.mandarin.features.savedadresses.data.remote.AddressRemoteDataSource
import com.mandarinkafe.mandarin.features.savedadresses.data.remote.AddressRemoteDataSourceImpl
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SaveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository
import com.mandarinkafe.mandarin.features.savedadresses.domain.impl.GetSavedAddressesUseCaseImpl
import com.mandarinkafe.mandarin.features.savedadresses.domain.impl.RemoveAddressUseCaseImpl
import com.mandarinkafe.mandarin.features.savedadresses.domain.impl.SaveAddressUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val addressModule = module {

    // Fused Location
    singleOf(::GetCurrentLocationUseCaseImpl) { bind<GetCurrentLocationUseCase>() }

    // Address Search Interactor
    singleOf(::AddressSearchInteractorImpl) { bind<AddressSearchInteractor>() }

    // Saved Addresses - Server API & Remote DataSource
    single {
        AddressServerApi(get(named(DiConstants.SERVER_AUTH_CLIENT_QUALIFIER)))
    }
    singleOf(::AddressRemoteDataSourceImpl) { bind<AddressRemoteDataSource>() }
    singleOf(::SavedAddressRepositoryImpl) { bind<SavedAddressRepository>() }
    singleOf(::SaveAddressUseCaseImpl) { bind<SaveAddressUseCase>() }
    singleOf(::RemoveAddressUseCaseImpl) { bind<RemoveAddressUseCase>() }
    singleOf(::GetSavedAddressesUseCaseImpl) { bind<GetSavedAddressesUseCase>() }

    // Delivery Areas
    singleOf(::GetDeliveryZoneUseCaseImpl) { bind<GetDeliveryZoneUseCase>() }
    singleOf(::DeliveryAreaRepositoryImpl) { bind<DeliveryAreaRepository>() }

    // ViewModel
    singleOf(::AddressDetailsViewModel)
    singleOf(::AddressViewModel)

}
