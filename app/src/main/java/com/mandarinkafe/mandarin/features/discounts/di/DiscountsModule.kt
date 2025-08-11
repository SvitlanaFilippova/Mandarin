package com.mandarinkafe.mandarin.features.discounts.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.db.CategoryDiscountQueries
import com.mandarinkafe.mandarin.features.discounts.data.impl.CategoryDiscountRepositoryImpl
import com.mandarinkafe.mandarin.features.discounts.data.local.CategoryDiscountsStorage
import com.mandarinkafe.mandarin.features.discounts.data.local.SQLDelightCategoryDiscountsStorage
import com.mandarinkafe.mandarin.features.discounts.domain.CheckDiscountByPhoneUseCaseImpl
import com.mandarinkafe.mandarin.features.discounts.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.discounts.domain.api.CheckDiscountByPhoneUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.LoyaltyCustomerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiscountsModule {

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
