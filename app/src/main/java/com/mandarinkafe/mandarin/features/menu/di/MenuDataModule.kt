package com.mandarinkafe.mandarin.features.menu.di

import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.features.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.features.menu.data.impl.MenuRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MenuDataModule {

    @Provides
    @Singleton
    fun provideMenuRepository(
        networkClient: NetworkClient,
        favoritesRepository: FavoritesRepository,
    ): MenuRepository {
        return MenuRepositoryImpl(
            networkClient = networkClient,
            favoritesRepository = favoritesRepository
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}