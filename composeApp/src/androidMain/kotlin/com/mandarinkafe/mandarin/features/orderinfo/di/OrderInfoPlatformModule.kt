package com.mandarinkafe.mandarin.features.orderinfo.di

import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val orderInfoPlatformModule = module {
    viewModelOf(::OrderInfoViewModel)
}

