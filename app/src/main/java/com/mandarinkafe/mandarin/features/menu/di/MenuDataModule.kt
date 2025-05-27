package com.mandarinkafe.mandarin.features.menu.di

import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.data.api.FavoritesReader
import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.features.menu.data.impl.BannersRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.ImageValidatorImpl
import com.mandarinkafe.mandarin.features.menu.data.impl.MenuRepositoryImpl
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
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
        favoritesReader: FavoritesReader,
    ): MenuRepositoryImpl {
        return MenuRepositoryImpl(
            networkClient = networkClient,
            favoritesReader = favoritesReader
        )
    }

    @Provides
    @Singleton
    fun provideMenuRepositoryInterface(
        impl: MenuRepositoryImpl
    ): MenuRepository = impl

    @Provides
    @Singleton
    fun provideMenuFetcherInterface(
        impl: MenuRepositoryImpl
    ): MenuFetcher = impl


    @Provides
    @Singleton
    fun provideBannersRepository(
        networkClient: NetworkClient,
        imageValidator: ImageValidator
    ): BannersRepository {
        return BannersRepositoryImpl(
            networkClient = networkClient,
            imageValidator = imageValidator
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideImageValidator(): ImageValidator {
        return ImageValidatorImpl()
    }

}