package com.mandarinkafe.mandarin.cart.di

import android.content.SharedPreferences
import com.mandarinkafe.mandarin.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.cart.data.sharedprefs.CartStorage
import com.mandarinkafe.mandarin.cart.data.sharedprefs.CartStorageImpl
import com.mandarinkafe.mandarin.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.cart.domain.impl.CartInteractorImpl
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import com.mandarinkafe.mandarin.di.Recommends
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.usecase.CategoryFilter
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
    fun provideCartRepository(
        @Recommends recommendsFilter: CategoryFilter,
        cartStorage: CartStorage,
        menuRepository: MenuRepository,
    ): CartRepository =
        CartRepositoryImpl(
            storage = cartStorage,
            menuRepository = menuRepository,
            recommendsFilter = recommendsFilter,
        )

    @Provides
    @Singleton
    fun provideCartInteractor(repository: CartRepository): CartInteractor =
        CartInteractorImpl(
            repository = repository
        )

    @Provides
    @Singleton
    fun provideCartStorage(sharedPreferences: SharedPreferences): CartStorage {
        return CartStorageImpl(
            sharedPreferences = sharedPreferences
        )
    }
}