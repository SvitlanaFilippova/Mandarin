package com.mandarinkafe.mandarin.features.address.di

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
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
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AddressModule {
    @Provides
    @Singleton
    fun provideMapKit(@ApplicationContext context: Context): MapKit {
        MapKitFactory.initialize(context)
        return MapKitFactory.getInstance()
    }

    @Provides
    @Singleton
    fun provideSearchManager(mapkit: MapKit): SearchManager { // mapKit для гарантии инициализации
        return SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(fusedProvider: FusedLocationProviderClient): FusedLocationRepository {
        return FusedLocationRepositoryImpl(
            fusedProvider = fusedProvider
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentLocationUseCase(repository: FusedLocationRepository): GetCurrentLocationUseCase {
        return GetCurrentLocationUseCaseImpl(
            repository = repository
        )
    }

    @Provides
    @Singleton
    fun provideAddressRepository(searchManager: SearchManager): AddressRepository {
        return AddressRepositoryImpl(
            searchManager = searchManager
        )
    }

    @Provides
    @Singleton
    fun provideAddressSearchInteractor(repository: AddressRepository): AddressSearchInteractor {
        return AddressSearchInteractorImpl(
            repository = repository
        )
    }

    @Provides
    @Singleton
    fun provideAddressStorage(sharedPreferences: SharedPreferences): AddressStorage {
        return AddressStorageImpl(
            sharedPreferences = sharedPreferences
        )
    }

    @Provides
    @Singleton
    fun provideSavedAddressRepository(storage: AddressStorage): SavedAddressRepository {
        return SavedAddressRepositoryImpl(
            storage = storage
        )
    }

    @Provides
    @Singleton
    fun provideSaveAddressUseCase(repository: SavedAddressRepository): SaveAddressUseCase {
        return SaveAddressUseCaseImpl(
            repository = repository
        )
    }

    @Provides
    @Singleton
    fun provideRemoveAddressUseCase(repository: SavedAddressRepository): RemoveAddressUseCase {
        return RemoveAddressUseCaseImpl(
            repository = repository
        )
    }

    @Provides
    @Singleton
    fun provideGetSavedAddressesUseCase(repository: SavedAddressRepository): GetSavedAddressesUseCase {
        return GetSavedAddressesUseCaseImpl(
            repository = repository
        )
    }

    @Singleton
    @Provides
    fun provideDeliveryAreaRepository(
        networkClient: GoogleDocsNetworkClient,
        menuCache: MenuCache
    ): DeliveryAreaRepository {
        return DeliveryAreaRepositoryImpl(
            networkClient = networkClient,
            menuCache = menuCache
        )
    }

    @Provides
    fun provideGetDeliveryZoneUseCase(repository: DeliveryAreaRepository): GetDeliveryZoneUseCase {
        return GetDeliveryZoneUseCaseImpl(
            deliveryAreaRepository = repository,
        )
    }
}