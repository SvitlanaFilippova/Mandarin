package com.mandarinkafe.mandarin.features.order.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.impl.LoyaltyCustomerRepositoryImpl
import com.mandarinkafe.mandarin.features.order.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.order.domain.impl.CheckDiscountByPhoneUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
}