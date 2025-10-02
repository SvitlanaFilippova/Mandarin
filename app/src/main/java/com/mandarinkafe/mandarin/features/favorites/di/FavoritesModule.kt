package com.mandarinkafe.mandarin.features.favorites.di

import android.content.SharedPreferences
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.favorites.data.impl.FavoritesRepositoryImpl
import com.mandarinkafe.mandarin.features.favorites.data.impl.FavoritesValidator
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorage
import com.mandarinkafe.mandarin.features.favorites.data.sharedprefs.FavoritesStorageImpl
import com.mandarinkafe.mandarin.features.favorites.domain.impl.FavoritesInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FavoritesModule {

    @Provides
    @Singleton
    fun provideLocalStorage(sharedPreferences: SharedPreferences): FavoritesStorage {
        return FavoritesStorageImpl(sharedPreferences = sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(
        validator: FavoritesValidator,
        storage: FavoritesStorage
    ): FavoritesRepositoryImpl =
        FavoritesRepositoryImpl(
            storage = storage,
            validator = validator,
        )

    @Provides
    @Singleton
    fun provideFavoritesReader(
        repoImpl: FavoritesRepositoryImpl
    ): FavoritesReader =
        repoImpl

    @Provides
    @Singleton
    fun provideFavoritesWriter(
        repoImpl: FavoritesRepositoryImpl
    ): FavoritesWriter =
        repoImpl

    @Provides
    @Singleton
    fun provideFavoritesApi(
        reader: FavoritesReader,
        writer: FavoritesWriter,

        forceRefreshMenu: ForceRefreshMenuUseCase
    ): FavoritesApi {
        return FavoritesInteractorImpl(
            reader = reader,
            writer = writer,
            forceRefreshMenu = forceRefreshMenu
        )
    }

    @Provides
    @Singleton
    fun provideValidateFavoritesUseCase(
        menuCache: MenuCache
    ): FavoritesValidator {
        return FavoritesValidator(
            menuCache = menuCache
        )
    }
}
