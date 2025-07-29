package com.mandarinkafe.mandarin.features.order.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.impl.LoyaltyCustomerRepositoryImpl
import com.mandarinkafe.mandarin.features.order.data.impl.OrderRepositoryImpl
import com.mandarinkafe.mandarin.features.order.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.impl.CheckDiscountByPhoneUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.GetPaymentTypesUseCaseImpl
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

    @Provides
    fun provideCheckDiscountByPhoneUseCase(
        repository: LoyaltyCustomerRepository
    ): CheckDiscountByPhoneUseCase {
        return CheckDiscountByPhoneUseCaseImpl(
            repository = repository
        )
    }

    @Singleton
    @Provides
    fun provideGetPaymentTypesUseCase(
        repository: OrderRepository
    ): GetPaymentTypesUseCase {
        return GetPaymentTypesUseCaseImpl(
            repository = repository
        )
    }

    @Singleton
    @Provides
    fun provideOrderRepository(
        networkClient: IikoNetworkClient
    ): OrderRepository {
        return OrderRepositoryImpl(
            networkClient = networkClient
        )
    }

}