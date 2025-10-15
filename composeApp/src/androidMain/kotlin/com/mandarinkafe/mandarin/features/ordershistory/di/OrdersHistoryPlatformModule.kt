package com.mandarinkafe.mandarin.features.ordershistory.di

import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ordersHistoryPlatformModule = module {
    viewModelOf(::OrdersHistoryViewModel)
}

