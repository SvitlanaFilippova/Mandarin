package com.mandarinkafe.mandarin.features.favorites.di

import android.content.Context
import android.content.SharedPreferences
import com.mandarinkafe.mandarin.core.data.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.features.favorites.data.impl.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorageImpl
import com.mandarinkafe.mandarin.features.favorites.domain.impl.FavoritesInteractorImpl
import com.mandarinkafe.mandarin.util.Constants.LOCAL_STORAGE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FavoritesModule {

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
    fun provideLocalStorage(sharedPreferences: SharedPreferences): FavoritesStorage {
        return FavoritesStorageImpl(sharedPreferences = sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(
        storage: FavoritesStorage
    ): FavoritesRepositoryImpl =
        FavoritesRepositoryImpl(storage)

    @Provides
    @Singleton
    fun provideFavoritesReader(
        repoImpl: FavoritesRepositoryImpl
    ): FavoritesReader =
        repoImpl

    @Provides
    @Singleton
    fun provideFavoritesInteractor(
        repository: FavoritesRepositoryImpl
    ): FavoritesInteractorImpl =
        FavoritesInteractorImpl(repository)

    @Provides
    @Singleton
    fun provideFavoritesApi(
        interactorImpl: FavoritesInteractorImpl
    ): FavoritesApi =
        interactorImpl
}
