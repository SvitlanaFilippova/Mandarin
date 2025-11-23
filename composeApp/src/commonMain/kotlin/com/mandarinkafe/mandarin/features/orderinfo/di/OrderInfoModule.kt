package com.mandarinkafe.mandarin.features.orderinfo.di

import com.mandarinkafe.mandarin.features.orderinfo.data.impl.ChangeOrderRepositoryImpl
import com.mandarinkafe.mandarin.features.orderinfo.data.impl.OrderInfoRepositoryImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.AddPaymentToOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangePaymentMethodUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ForceRefreshOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusFromIikoUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.RepeatOrderInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.AddPaymentToOrderUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.CancelOrderUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.ChangePaymentMethodUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.ForceRefreshOrderStatusUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.GetOrderStatusFromIikoUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.GetOrderStatusUseCaseImpl
import com.mandarinkafe.mandarin.features.orderinfo.domain.impl.RepeatOrderInteractorImpl
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orderInfoModule = module {

    // --- Repositories ---
    singleOf(::OrderInfoRepositoryImpl) { bind<OrderInfoRepository>() }
    singleOf(::ChangeOrderRepositoryImpl) { bind<ChangeOrderRepository>() }

    // --- UseCases / Interactors ---
    singleOf(::GetOrderStatusUseCaseImpl) { bind<GetOrderStatusUseCase>() }
    singleOf(::GetOrderStatusFromIikoUseCaseImpl) { bind<GetOrderStatusFromIikoUseCase>() }
    singleOf(::ForceRefreshOrderStatusUseCaseImpl) { bind<ForceRefreshOrderStatusUseCase>() }
    singleOf(::CancelOrderUseCaseImpl) { bind<CancelOrderUseCase>() }
    singleOf(::RepeatOrderInteractorImpl) { bind<RepeatOrderInteractor>() }
    singleOf(::AddPaymentToOrderUseCaseImpl) { bind<AddPaymentToOrderUseCase>() }
    singleOf(::ChangePaymentMethodUseCaseImpl) { bind<ChangePaymentMethodUseCase>() }

    // --- ViewModel ---
    factoryOf(::OrderInfoViewModel)

}





