package com.mandarinkafe.mandarin.di

import com.mandarinkafe.mandarin.cart.data.impl.CartRepositoryImpl
import com.mandarinkafe.mandarin.cart.domain.api.CartRepository
import com.mandarinkafe.mandarin.cart.domain.impl.CartInteractorImpl
import com.mandarinkafe.mandarin.cart.domain.usecase.CartInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @AppScope
    fun provideCartRepository(): CartRepository = CartRepositoryImpl()

    @Provides
    @AppScope
    fun provideCartUseCase(repository: CartRepository): CartInteractor =
        CartInteractorImpl(repository)
}