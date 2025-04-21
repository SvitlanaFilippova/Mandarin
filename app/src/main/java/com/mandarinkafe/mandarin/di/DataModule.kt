package com.mandarinkafe.mandarin.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.data.network.IikoApiService
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.data.network.RetrofitNetworkClient
import com.mandarinkafe.mandarin.favorites.data.impl.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.favorites.data.sharedprefs.LocalStorage
import com.mandarinkafe.mandarin.favorites.data.sharedprefs.LocalStorageImpl
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.data.impl.MenuRepositoryImpl
import com.mandarinkafe.mandarin.menu.data.mapper.DtoToDomainConverter
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.LOCAL_STORAGE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideIikoApiService(): IikoApiService {
        return Retrofit.Builder()
            .baseUrl(IIKO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(
                IikoApiService::
                class.java
            )
    }

    @Provides
    @Singleton
    fun provideRetrofitNetworkClient(
        @ApplicationContext
        context: Context,
        ikkoService: IikoApiService
    ): NetworkClient {
        return RetrofitNetworkClient(context = context, ikkoService = ikkoService)
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
    fun provideLocalStorage(sharedPreferences: SharedPreferences): LocalStorage {
        return LocalStorageImpl(sharedPreferences = sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(localStorage: LocalStorage): FavoritesRepository {
        return FavoritesRepositoryImpl(localStorage = localStorage)
    }

    @Provides
    @Singleton
    fun provideMenuRepository(
        networkClient: NetworkClient,
        converter: DtoToDomainConverter,
        @ApplicationContext
        context: Context
    ): MenuRepository {
        return MenuRepositoryImpl(
            networkClient = networkClient,
            converter = converter,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideDtoToDomainConverter(favoritesRepository: FavoritesRepository): DtoToDomainConverter {
        return DtoToDomainConverter(favoritesRepository = favoritesRepository)
    }
}