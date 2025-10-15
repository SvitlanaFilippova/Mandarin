package com.mandarinkafe.mandarin.features.ordershistory.di

import com.mandarinkafe.mandarin.features.order.domain.api.SaveOrderToHistoryUseCase
import com.mandarinkafe.mandarin.features.ordershistory.data.impl.OrdersHistoryRepositoryImpl
import com.mandarinkafe.mandarin.features.ordershistory.data.impl.OrdersStatusesRepositoryImpl
import com.mandarinkafe.mandarin.features.ordershistory.data.local.OrdersHistoryStorage
import com.mandarinkafe.mandarin.features.ordershistory.data.local.SQLDelightOrdersHistoryStorage
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.GetOrdersStatusesUseCase
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersStatusesRepository
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.GetOrdersStatusesUseCaseImpl
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.OrdersHistoryInteractorImpl
import com.mandarinkafe.mandarin.features.ordershistory.domain.impl.SaveOrderToHistoryUseCaseImpl
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import com.mandarinkafe.mandarin.shared.database.AppDatabase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ordersHistoryModule = module {

    // --- Storage ---
    single { get<AppDatabase>().savedOrderQueries }
    singleOf(::SQLDelightOrdersHistoryStorage) { bind<OrdersHistoryStorage>() }

    // --- Repositories ---
    singleOf(::OrdersHistoryRepositoryImpl) { bind<OrdersHistoryRepository>() }
    singleOf(::OrdersStatusesRepositoryImpl) { bind<OrdersStatusesRepository>() }

    // --- UseCases / Interactors ---
    singleOf(::SaveOrderToHistoryUseCaseImpl) { bind<SaveOrderToHistoryUseCase>() }
    singleOf(::OrdersHistoryInteractorImpl) { bind<OrdersHistoryInteractor>() }
    singleOf(::GetOrdersStatusesUseCaseImpl) { bind<GetOrdersStatusesUseCase>() }

    // --- ViewModel ---
    singleOf(::OrdersHistoryViewModel)
}





