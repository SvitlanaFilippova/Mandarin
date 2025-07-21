package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.api.CartCountReader
import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.data.impl.GetInitialDataUseCaseImpl
import com.mandarinkafe.mandarin.core.data.impl.MenuCacheImpl
import com.mandarinkafe.mandarin.core.data.impl.ObserveCartCountUseCaseImpl
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsApiService
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClientImpl
import com.mandarinkafe.mandarin.core.data.network.IikoApiService
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClientImpl
import com.mandarinkafe.mandarin.core.data.network.NominatimApiService
import com.mandarinkafe.mandarin.core.data.network.NominatimNetworkClient
import com.mandarinkafe.mandarin.core.data.network.NominatimNetworkClientImpl
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.util.Constants.GOOGLE_DOCS_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreDataModule {

    @Provides
    @Singleton
    fun provideNominatimApiService(): NominatimApiService {
        return Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE_URL)
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
    fun provideIikoApiService(): IikoApiService {
        return Retrofit.Builder()
            .baseUrl(IIKO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(
                IikoApiService::class.java
            )
    }

    @Provides
    @Singleton
    fun provideGoogleDocsApiService(): GoogleDocsApiService {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_DOCS_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GoogleDocsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIikoNetworkClient(
        ikkoService: IikoApiService,
        networkMonitor: NetworkMonitor
    ): IikoNetworkClient {
        return IikoNetworkClientImpl(
            networkMonitor = networkMonitor,
            iikoService = ikkoService,
        )
    }

    @Provides
    @Singleton
    fun provideGoogleDocsNetworkClient(
        googleDocsApi: GoogleDocsApiService,
        networkMonitor: NetworkMonitor
    ): GoogleDocsNetworkClient {
        return GoogleDocsNetworkClientImpl(
            networkMonitor = networkMonitor,
            googleDocsApi = googleDocsApi
        )
    }

    @Provides
    @Singleton
    fun provideNominatimNetworkClient(
        nominatimApiService: NominatimApiService,
        networkMonitor: NetworkMonitor
    ): NominatimNetworkClient {
        return NominatimNetworkClientImpl(
            networkMonitor = networkMonitor,
            nominatimApiService = nominatimApiService
        )
    }

    @Provides
    @Singleton
    fun provideMenuCache(fetcher: MenuFetcher): MenuCache {
        return MenuCacheImpl(
            fetcher = fetcher
        )
    }

    @Provides
    @Singleton
    fun provideGetInitialDataUseCase(
        menuCache: MenuCache,
        bannersRepository: BannersRepository
    ): GetInitialDataUseCase {
        return GetInitialDataUseCaseImpl(
            menuCache = menuCache,
            bannersRepository = bannersRepository,
        )
    }

    @Provides
    @Singleton
    fun provideObserveCartCountUseCase(cartCountReader: CartCountReader): ObserveCartCountUseCase {
        return ObserveCartCountUseCaseImpl(
            reader = cartCountReader
        )
    }
}