package com.mandarinkafe.mandarin.features.cart.di

import android.content.SharedPreferences
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.impl.RecommendsSchemaRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.sharedprefs.CartStorageImpl
import com.mandarinkafe.mandarin.features.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.impl.CartInteractorImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.GetRecommendsUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.di.Recommends
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @Singleton
    fun provideCartStorage(sharedPreferences: SharedPreferences): CartStorage {
        return CartStorageImpl(
            sharedPreferences = sharedPreferences
        )
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        @Recommends recommendsFilter: CategoryFilter,
        cartStorage: CartStorage,
        menuCache: MenuCache,
    ): CartRepository =
        CartRepositoryImpl(
            storage = cartStorage,
            menuCache = menuCache,
            recommendsFilter = recommendsFilter,
        )

    @Provides
    @Singleton
    fun provideRecommendsSchemaRepository(
        networkClient: NetworkClient
    ): RecommendsSchemaRepository =
        RecommendsSchemaRepositoryImpl(
            networkClient = networkClient
        )

    @Provides
    @Singleton
    fun provideCartInteractor(repository: CartRepository): CartInteractor =
        CartInteractorImpl(
            repository = repository
        )

    @Provides
    @Singleton
    fun provideGetRecommendsUseCaseImpl(
        menuCache: MenuCache,
        schemaRepository: RecommendsSchemaRepository
    ): GetRecommendsUseCase = GetRecommendsUseCaseImpl(
        schemaRepository = schemaRepository,
        menuCache = menuCache,
    )
}