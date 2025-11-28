package com.mandarinkafe.mandarin.features.ordershistory.di

import com.mandarinkafe.mandarin.core.di.DiConstants
import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.data.impl.OrdersHistoryRepositoryImpl
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.features.ordershistory.data.remote.OrdersHistoryRemoteDataSource
import com.mandarinkafe.mandarin.features.ordershistory.data.remote.OrdersHistoryRemoteDataSourceImpl
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.OrdersHistoryInteractorImpl
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.SaveOrderToHistoryUseCaseImpl
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ordersHistoryModule = module {

    // --- Server API & Remote DataSource ---
    single {
        OrdersHistoryServerApi(
            get(named(DiConstants.SERVER_AUTH_CLIENT_QUALIFIER)),
            get()
        )
    }
    singleOf(::OrdersHistoryRemoteDataSourceImpl) { bind<OrdersHistoryRemoteDataSource>() }

    // --- Repositories ---
    singleOf(::OrdersHistoryRepositoryImpl) { bind<OrdersHistoryRepository>() }

    // --- UseCases / Interactors ---
    singleOf(::SaveOrderToHistoryUseCaseImpl) { bind<SaveOrderToHistoryUseCase>() }
    singleOf(::OrdersHistoryInteractorImpl) { bind<OrdersHistoryInteractor>() }

    // --- ViewModel ---
    singleOf(::OrdersHistoryViewModel)
}





