package com.mandarinkafe.mandarin.features.order.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.features.infrastructure.data.impl.LoyaltyCustomerRepositoryImpl
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.order.data.impl.OrderRepositoryImpl
import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CalculateCartTotalWithDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase
import com.mandarinkafe.mandarin.features.order.domain.impl.ApplyPhoneDiscountUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.CalculateCartTotalWithDiscountUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.CreateOrderUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.ResolvePickupPointUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OrderModule {

    @Provides
    fun provideLoyaltyCustomerRepository(
        iikoNetworkClient: IikoNetworkClient,
    ): LoyaltyCustomerRepository {
        return LoyaltyCustomerRepositoryImpl(
            networkClient = iikoNetworkClient,
        )
    }


    @Singleton
    @Provides
    fun provideOrderRepository(
        networkClient: IikoNetworkClient,
        menuCache: MenuCache
    ): OrderRepository {
        return OrderRepositoryImpl(
            networkClient = networkClient,
            menuCache = menuCache
        )
    }

    @Singleton
    @Provides
    fun provideCreateOrderUseCase(
        repository: OrderRepository,
        menuCache: MenuCache
    ): CreateOrderUseCase {
        return CreateOrderUseCaseImpl(
            repository = repository,
            menuCache = menuCache
        )
    }

    @Singleton
    @Provides
    fun provideCalculateCartTotalWithDiscountUseCase(): CalculateCartTotalWithDiscountUseCase {
        return CalculateCartTotalWithDiscountUseCaseImpl()
    }

    @Singleton
    @Provides
    fun provideResolvePickupPointUseCase(): ResolvePickupPointUseCase {
        return ResolvePickupPointUseCaseImpl()
    }

    @Singleton
    @Provides
    fun provideApplyPhoneDiscountUseCase(checkDiscountByPhone: CheckDiscountByPhoneUseCase): ApplyPhoneDiscountUseCase {
        return ApplyPhoneDiscountUseCaseImpl(
            checkDiscountByPhone = checkDiscountByPhone
        )
    }
}