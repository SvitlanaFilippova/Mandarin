package com.mandarinkafe.mandarin.features.orderinfo.di

import com.mandarinkafe.mandarin.features.orderinfo.data.impl.ChangeOrderRepositoryImpl
import com.mandarinkafe.mandarin.features.orderinfo.data.impl.OrderInfoRepositoryImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.RepeatOrderInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.CancelOrderUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.GetOrderStatusUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.RepeatOrderInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orderInfoModule = module {

    // --- Repositories ---
    singleOf(::OrderInfoRepositoryImpl) { bind<OrderInfoRepository>() }
    singleOf(::ChangeOrderRepositoryImpl) { bind<ChangeOrderRepository>() }

    // --- UseCases / Interactors ---
    singleOf(::GetOrderStatusUseCaseImpl) { bind<GetOrderStatusUseCase>() }
    singleOf(::CancelOrderUseCaseImpl) { bind<CancelOrderUseCase>() }
    singleOf(::RepeatOrderInteractorImpl) { bind<RepeatOrderInteractor>() }
}

