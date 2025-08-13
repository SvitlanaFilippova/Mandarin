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
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoDiscountApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoMenuApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoOrderApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoTerminalApi
import com.mandarinkafe.mandarin.core.data.network.impl.GoogleDocsNetworkClientImpl
import com.mandarinkafe.mandarin.core.data.network.impl.IikoNetworkClientImpl
import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartCountUseCase
import com.mandarinkafe.mandarin.core.domain.api.ObserveCartItemsUseCase
import com.mandarinkafe.mandarin.core.domain.impl.GetInitialDataUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.ObserveCartCountUseCaseImpl
import com.mandarinkafe.mandarin.core.domain.impl.ObserveCartItemsUseCaseImpl
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.features.discounts.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.util.Constants.DATABASE_NAME
import com.mandarinkafe.mandarin.util.Constants.GOOGLE_DOCS_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.LOCAL_STORAGE_NAME
import com.mandarinkafe.mandarin.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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
        authApi: IikoAuthApi,
        menuApi: IikoMenuApi,
        orderApi: IikoOrderApi,
        discountApi: IikoDiscountApi,
        terminalApi: IikoTerminalApi,
        networkMonitor: NetworkMonitor
    ): IikoNetworkClient {
        return IikoNetworkClientImpl(
            authApi = authApi,
            menuApi = menuApi,
            orderApi = orderApi,
            discountApi = discountApi,
            terminalApi = terminalApi,
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
    fun provideGetInitialDataUseCase(
        menuCache: MenuCache,
        bannersRepository: BannersRepository,
        categoryDiscountRepository: CategoryDiscountRepository
    ): GetInitialDataUseCase {
        return GetInitialDataUseCaseImpl(
            menuCache = menuCache,
            bannersRepository = bannersRepository,
            categoryDiscountRepository = categoryDiscountRepository,
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