package com.mandarinkafe.mandarin.features.orderconfirmation.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.orderconfirmation.data.impl.OrderInfoRepositoryImpl
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.impl.ObserveOrderStatusUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class OrderConfirmationModule {
    @Provides
    fun provideOrderInfoRepository(
        iikoNetworkClient: IikoNetworkClient,
        menuCache: MenuCache
    ): OrderInfoRepository {
        return OrderInfoRepositoryImpl(
            networkClient = iikoNetworkClient,
            menuCache = menuCache,
        )
    }

    @Provides
    fun provideObserveOrderStatusUseCase(
        repository: OrderInfoRepository,
    ): ObserveOrderStatusUseCase {
        return ObserveOrderStatusUseCaseImpl(
            repository = repository,
        )
    }
}