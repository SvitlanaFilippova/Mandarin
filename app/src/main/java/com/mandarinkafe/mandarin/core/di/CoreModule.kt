package com.mandarinkafe.mandarin.core.di

import android.content.Context
import android.content.SharedPreferences
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.data.impl.MenuCacheImpl
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsApiService
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.IikoApi
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.core.data.network.impl.GoogleDocsNetworkClientImpl
import com.mandarinkafe.mandarin.core.data.network.impl.IikoNetworkClientImpl
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.impl.ForceRefreshMenuUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.GetInitialDataUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.ObserveCartCountUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.ObserveCartItemsUseCaseImpl
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Constants.DATABASE_NAME
import com.mandarinkafe.mandarin.util.Constants.LOCAL_STORAGE_NAME
import com.mandarinkafe.mandarin.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase {
        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = ctx,
            name = DATABASE_NAME
        )
        return AppDatabase(driver)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext
        context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(LOCAL_STORAGE_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @GoogleDocsClient
    fun provideGoogleDocsHttpClient(): HttpClient {
        return HttpClient {
            defaultRequest {
                contentType(ContentType.Text.Plain)
            }
        }
    }

    @Provides
    @Singleton
    fun provideGoogleDocsApiService(
        @GoogleDocsClient client: HttpClient
    ): GoogleDocsApiService {
        return GoogleDocsApiService(client)
    }

    @Provides
    @Singleton
    fun provideIikoNetworkClient(
        menuApi: ServerApi,
        iikoApi: IikoApi,
        networkMonitor: NetworkMonitor
    ): IikoNetworkClient {
        return IikoNetworkClientImpl(
            iikoApi = iikoApi,
            menuApi = menuApi,
            networkMonitor = networkMonitor,
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
    fun provideMenuCache(fetcher: MenuFetcher): MenuCache {
        return MenuCacheImpl(
            fetcher = fetcher
        )
    }

    @Provides
    @Singleton
    fun provideForceRefreshMenuUseCase(
        repository: MenuRepository,
        cache: MenuCache
    ): ForceRefreshMenuUseCase {
        return ForceRefreshMenuUseCaseImpl(
            repository = repository,
            cache = cache
        )
    }

    @Provides
    @Singleton
    fun provideGetInitialDataUseCase(
        menuCache: MenuCache,
        bannersRepository: BannersRepository,
        categoryDiscountRepository: CategoryDiscountRepository,
        deliveryAreaRepository: DeliveryAreaRepository,
    ): GetInitialDataUseCase {
        return GetInitialDataUseCaseImpl(
            menuCache = menuCache,
            bannersRepository = bannersRepository,
            categoryDiscountRepository = categoryDiscountRepository,
            deliveryAreaRepository = deliveryAreaRepository
        )
    }

    @Provides
    @Singleton
    fun provideObserveCartCountUseCase(cartReader: CartReader): ObserveCartCountUseCase {
        return ObserveCartCountUseCaseImpl(
            reader = cartReader
        )
    }

    @Provides
    @Singleton
    fun provideObserveCartItemsUseCase(cartReader: CartReader): ObserveCartItemsUseCase {
        return ObserveCartItemsUseCaseImpl(reader = cartReader)
    }
}