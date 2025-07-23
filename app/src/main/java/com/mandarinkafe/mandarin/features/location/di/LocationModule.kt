package com.mandarinkafe.mandarin.features.location.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mandarinkafe.mandarin.features.location.data.impl.AddressRepositoryImpl
import com.mandarinkafe.mandarin.features.location.data.impl.FusedLocationRepositoryImpl
import com.mandarinkafe.mandarin.features.location.domain.api.AddressRepository
import com.mandarinkafe.mandarin.features.location.domain.api.FusedLocationRepository
import com.mandarinkafe.mandarin.features.location.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.location.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.location.domain.impl.GetAddressByPointUseCaseImpl
import com.mandarinkafe.mandarin.features.location.domain.impl.GetCurrentLocationUseCaseImpl
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
class LocationModule {

    @Provides
    @Singleton
    fun provideSearchManager(): SearchManager {
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
    fun provideGetAddressByPointUseCase(repository: AddressRepository): GetAddressByPointUseCase {
        return GetAddressByPointUseCaseImpl(
            addressRepository = repository
        )
    }

}