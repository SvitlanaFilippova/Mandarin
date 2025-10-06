package com.mandarinkafe.mandarin.features.cart.di

import com.mandarinkafe.mandarin.core.data.api.CartReader
import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.ClearCartUseCase
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.db.CartItemsQueries
import com.mandarinkafe.mandarin.features.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.impl.RecommendsSchemaRepositoryImpl
import com.mandarinkafe.mandarin.features.cart.data.local.CartStorage
import com.mandarinkafe.mandarin.features.cart.data.local.SQLDelightCartStorage
import com.mandarinkafe.mandarin.features.cart.domain.api.CartWriter
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.impl.CartInteractorImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.ClearCartUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.domain.impl.GetRecommendsUseCaseImpl
import com.mandarinkafe.mandarin.features.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    fun provideCartQueries(db: AppDatabase): CartItemsQueries =
        db.cartItemsQueries

    @Provides
    @Singleton
    fun provideCartStorage(queries: CartItemsQueries): CartStorage {
        return SQLDelightCartStorage(queries = queries)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        cartStorage: CartStorage,
        menuCache: MenuCache,
    ): CartWriter =
        CartRepositoryImpl(
            storage = cartStorage,
            menuCache = menuCache,
        )

    @Provides
    @Singleton
    fun provideCartCountReader(
        cartWriter: CartWriter
    ): CartReader = cartWriter as CartReader

    @Provides
    @Singleton
    fun provideRecommendsSchemaRepository(
        networkClient: ServerNetworkClient
    ): RecommendsSchemaRepository =
        RecommendsSchemaRepositoryImpl(
            networkClient = networkClient
        )

    @Provides
    @Singleton
    fun provideCartInteractor(
        writer: CartWriter,
        reader: CartReader,
        forceRefreshMenu: ForceRefreshMenuUseCase
    ): CartInteractor =
        CartInteractorImpl(
            cartWriter = writer,
            cartReader = reader,
            forceRefreshMenu = forceRefreshMenu
        )

    @Provides
    @Singleton
    fun provideGetRecommendsUseCase(
        menuCache: MenuCache,
        schemaRepository: RecommendsSchemaRepository
    ): GetRecommendsUseCase = GetRecommendsUseCaseImpl(
        schemaRepository = schemaRepository,
        menuCache = menuCache,
    )

    @Provides
    @Singleton
    fun provideClearCartUseCase(cartWriter: CartWriter): ClearCartUseCase {
        return ClearCartUseCaseImpl(
            repository = cartWriter
        )
    }
}
