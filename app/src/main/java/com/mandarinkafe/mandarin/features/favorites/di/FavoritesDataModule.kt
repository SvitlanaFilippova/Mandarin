package com.mandarinkafe.mandarin.features.favorites.di

import android.content.Context
import android.content.SharedPreferences
import com.mandarinkafe.mandarin.features.favorites.data.impl.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorageImpl
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.util.Constants.LOCAL_STORAGE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FavoritesDataModule {

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
    fun provideFavoritesRepository(favoritesStorage: FavoritesStorage): FavoritesRepository {
        return FavoritesRepositoryImpl(favoritesStorage = favoritesStorage)
    }
}