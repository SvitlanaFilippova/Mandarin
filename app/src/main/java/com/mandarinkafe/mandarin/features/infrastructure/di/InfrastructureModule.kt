package com.mandarinkafe.mandarin.features.infrastructure.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.db.CategoryDiscountQueries
import com.mandarinkafe.mandarin.features.infrastructure.data.impl.AliveTerminalRepositoryImpl
import com.mandarinkafe.mandarin.features.infrastructure.data.impl.CategoryDiscountRepositoryImpl
import com.mandarinkafe.mandarin.features.infrastructure.data.impl.PaymentTypesRepositoryImpl
import com.mandarinkafe.mandarin.features.infrastructure.data.local.CategoryDiscountsStorage
import com.mandarinkafe.mandarin.features.infrastructure.data.local.SQLDelightCategoryDiscountsStorage
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.AliveTerminalRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CheckIfTerminalIsAliveUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.PaymentTypesRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.impl.CheckDiscountByPhoneUseCaseImpl
import com.mandarinkafe.mandarin.features.infrastructure.domain.impl.CheckIfTerminalIsAliveUseCaseImpl
import com.mandarinkafe.mandarin.features.infrastructure.domain.impl.GetPaymentTypesUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InfrastructureModule {
    @Singleton
    @Provides
    fun providePaymentTypesRepository(
        networkClient: IikoNetworkClient
    ): PaymentTypesRepository {
        return PaymentTypesRepositoryImpl(
            networkClient = networkClient
        )
    }

    @Singleton
    @Provides
    fun provideGetPaymentTypesUseCase(
        repository: PaymentTypesRepository
    ): GetPaymentTypesUseCase {
        return GetPaymentTypesUseCaseImpl(
            repository = repository
        )
    }

    @Singleton
    @Provides
    fun provideAliveTerminalRepository(
        networkClient: IikoNetworkClient
    ): AliveTerminalRepository {
        return AliveTerminalRepositoryImpl(
            networkClient = networkClient
        )
    }

    @Singleton
    @Provides
    fun provideCheckIfTerminalIsAliveUseCase(
        repository: AliveTerminalRepository
    ): CheckIfTerminalIsAliveUseCase {
        return CheckIfTerminalIsAliveUseCaseImpl(repository = repository)
    }

    @Provides
    fun provideCategoryDiscountsQueries(db: AppDatabase): CategoryDiscountQueries =
        db.categoryDiscountQueries

    @Provides
    @Singleton
    fun provideCategoryDiscountsStorage(queries: CategoryDiscountQueries): CategoryDiscountsStorage {
        return SQLDelightCategoryDiscountsStorage(queries = queries)
    }

    @Provides
    @Singleton
    fun provideCategoryDiscountRepository(
        storage: CategoryDiscountsStorage,
        networkClient: IikoNetworkClient
    ): CategoryDiscountRepository {
        return CategoryDiscountRepositoryImpl(
            storage = storage,
            networkClient = networkClient
        )
    }

    @Provides
    fun provideCheckDiscountByPhoneUseCase(
        loyaltyCustomerRepository: LoyaltyCustomerRepository,
        categoryDiscountRepository: CategoryDiscountRepository
    ): CheckDiscountByPhoneUseCase {
        return CheckDiscountByPhoneUseCaseImpl(
            repository = loyaltyCustomerRepository,
            categoryDiscountRepository = categoryDiscountRepository
        )
    }
}