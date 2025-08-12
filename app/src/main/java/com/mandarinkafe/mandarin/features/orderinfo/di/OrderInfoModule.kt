package com.mandarinkafe.mandarin.features.orderinfo.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.orderinfo.data.impl.ChangeOrderRepositoryImpl
import com.mandarinkafe.mandarin.features.orderinfo.data.impl.OrderInfoRepositoryImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetCurrentStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.CancelOrderUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.GetCurrentStatusUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.ObserveOrderStatusUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class OrderInfoModule {
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

    @Provides
    fun provideChangeOrderRepository(
        networkClient: IikoNetworkClient,
    ): ChangeOrderRepository {
        return ChangeOrderRepositoryImpl(
            networkClient = networkClient,
        )
    }

    @Provides
    fun provideCancelOrderUseCase(
        repository: ChangeOrderRepository,
    ): CancelOrderUseCase {
        return CancelOrderUseCaseImpl(
            repository = repository,
        )
    }

    @Provides
    fun provideGetCurrentStatusUseCase(
        repository: OrderInfoRepository,
    ): GetCurrentStatusUseCase {
        return GetCurrentStatusUseCaseImpl(
            repository = repository,
        )
    }

}