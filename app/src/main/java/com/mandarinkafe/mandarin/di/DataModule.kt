package com.mandarinkafe.mandarin.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.data.network.IikoApiService
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.data.network.RetrofitNetworkClient
import com.mandarinkafe.mandarin.menu.data.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.menu.data.LocalStorage
import com.mandarinkafe.mandarin.menu.data.MenuRepositoryImpl
import com.mandarinkafe.mandarin.menu.data.mapper.DtoToDomainConverter
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
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
            .baseUrl("https://api-ru.iiko.services")
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
        return context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideLocalStorage(sharedPreferences: SharedPreferences): LocalStorage {
        return LocalStorage(sharedPreferences = sharedPreferences)
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
        converter: DtoToDomainConverter
    ): MenuRepository {
        return MenuRepositoryImpl(networkClient = networkClient, converter = converter)
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