package com.mandarinkafe.mandarin.features.ordershistory.di

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.database.AppDatabase
import com.mandarinkafe.mandarin.db.SavedOrderQueries
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.data.impl.OrdersHistoryRepositoryImpl
import com.mandarinkafe.mandarin.features.ordershistory.data.impl.OrdersStatusesRepositoryImpl
import com.mandarinkafe.mandarin.features.ordershistory.data.local.OrdersHistoryStorage
import com.mandarinkafe.mandarin.features.ordershistory.data.local.SQLDelightOrdersHistoryStorage
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersStatusesUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersStatusesRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.GetOrdersHistoryUseCaseImpl
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.GetOrdersStatusesUseCaseImpl
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.SaveOrderToHistoryUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OrderHistoryModule {

    @Provides
    fun provideSavedOrderQueries(db: AppDatabase): SavedOrderQueries =
        db.savedOrderQueries

    @Provides
    @Singleton
    fun provideOrdersHistoryStorage(queries: SavedOrderQueries): OrdersHistoryStorage {
        return SQLDelightOrdersHistoryStorage(queries = queries)
    }

    @Provides
    @Singleton
    fun provideOrdersHistoryRepository(
        storage: OrdersHistoryStorage,
    ): OrdersHistoryRepository =
        OrdersHistoryRepositoryImpl(
            storage = storage,
        )

    @Provides
    @Singleton
    fun provideSaveOrderToHistoryUseCase(
        repository: OrdersHistoryRepository
    ): SaveOrderToHistoryUseCase =
        SaveOrderToHistoryUseCaseImpl(
            repository = repository,
        )

    @Provides
    @Singleton
    fun provideGetOrdersHistoryUseCase(
        repository: OrdersHistoryRepository
    ): GetOrdersHistoryUseCase =
        GetOrdersHistoryUseCaseImpl(
            repository = repository,
        )

    @Provides
    @Singleton
    fun provideOrdersStatusesRepository(
        networkClient: IikoNetworkClient
    ): OrdersStatusesRepository =
        OrdersStatusesRepositoryImpl(
            networkClient = networkClient
        )

    @Provides
    @Singleton
    fun provideGetOrdersStatusesUseCase(
        repository: OrdersStatusesRepository
    ): GetOrdersStatusesUseCase =
        GetOrdersStatusesUseCaseImpl(
            repository = repository,
        )
}