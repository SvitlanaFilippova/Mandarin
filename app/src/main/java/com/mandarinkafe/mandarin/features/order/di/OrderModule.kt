package com.mandarinkafe.mandarin.features.order.di

import com.mandarinkafe.mandarin.core.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.order.data.impl.GeocodingRepositoryImpl
import com.mandarinkafe.mandarin.features.order.data.network.GeocodingClient
import com.mandarinkafe.mandarin.features.order.data.network.NominatimApiService
import com.mandarinkafe.mandarin.features.order.data.network.impl.GeocodingClientImpl
import com.mandarinkafe.mandarin.features.order.domain.api.GeocodingRepository
import com.mandarinkafe.mandarin.features.order.domain.api.GetCoordinatesUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.impl.GetCoordinatesUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.GetDeliveryZoneUseCaseImpl
import com.mandarinkafe.mandarin.util.Constants.MANDARIN_CAFE
import com.mandarinkafe.mandarin.util.Constants.NOMINATIM_BASE_URL
import com.mandarinkafe.mandarin.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OrderModule {

    @Provides
    fun provideGetDeliveryZoneUseCase(repository: DeliveryAreaRepository): GetDeliveryZoneUseCase {
        return GetDeliveryZoneUseCaseImpl(
            deliveryAreaRepository = repository,
        )
    }

    @Provides
    @Singleton
    fun provideNominatimApiService(): NominatimApiService {
        return Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", MANDARIN_CAFE)
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(NominatimApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNominatimNetworkClient(
        nominatimApiService: NominatimApiService,
        networkMonitor: NetworkMonitor
    ): GeocodingClient {
        return GeocodingClientImpl(
            networkMonitor = networkMonitor,
            nominatimApiService = nominatimApiService
        )
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(
        geocodingClient: GeocodingClient,
    ): GeocodingRepository {
        return GeocodingRepositoryImpl(
            networkClient = geocodingClient
        )
    }

    @Provides
    fun provideGetCoordinatesUseCase(
        repository: GeocodingRepository,
    ): GetCoordinatesUseCase {
        return GetCoordinatesUseCaseImpl(
            repository = repository
        )
    }
}