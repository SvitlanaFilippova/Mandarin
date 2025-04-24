package com.mandarinkafe.mandarin.favorites.di

import android.content.Context
import android.content.SharedPreferences
import com.mandarinkafe.mandarin.favorites.data.impl.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.favorites.data.sharedprefs.LocalStorage
import com.mandarinkafe.mandarin.favorites.data.sharedprefs.LocalStorageImpl
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
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
    fun provideLocalStorage(sharedPreferences: SharedPreferences): LocalStorage {
        return LocalStorageImpl(sharedPreferences = sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(localStorage: LocalStorage): FavoritesRepository {
        return FavoritesRepositoryImpl(localStorage = localStorage)
    }
}