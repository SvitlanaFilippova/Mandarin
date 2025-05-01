package com.mandarinkafe.mandarin.menu.di

import android.content.Context
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.data.impl.MenuRepositoryImpl
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext
        context: Context
    ): MenuRepository {
        return MenuRepositoryImpl(
            networkClient = networkClient,
            context = context,
            favoritesRepository = favoritesRepository
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}