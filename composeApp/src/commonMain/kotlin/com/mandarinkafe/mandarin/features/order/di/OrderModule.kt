package com.mandarinkafe.mandarin.features.order.di

import com.mandarinkafe.mandarin.features.infrastructure.data.impl.LoyaltyCustomerRepositoryImpl
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.LoyaltyCustomerRepository
import com.mandarinkafe.mandarin.features.order.data.impl.OrderRepositoryImpl
import com.mandarinkafe.mandarin.features.order.domain.api.ApplyPhoneDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CalculateCartTotalWithDiscountUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.OrderRepository
import com.mandarinkafe.mandarin.features.order.domain.api.PickupOnlyRemoveUseCase
import com.mandarinkafe.mandarin.features.order.domain.api.ResolvePickupPointUseCase
import com.mandarinkafe.mandarin.features.order.domain.impl.ApplyPhoneDiscountUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.CalculateCartTotalWithDiscountUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.CreateOrderUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.PickupOnlyRemoveUseCaseImpl
import com.mandarinkafe.mandarin.features.order.domain.impl.ResolvePickupPointUseCaseImpl
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers.OrderCreator
import com.mandarinkafe.mandarin.features.savedadresses.domain.AddressUseCases
import com.mandarinkafe.mandarin.features.savedadresses.domain.CartContentUseCases
import com.mandarinkafe.mandarin.features.savedadresses.domain.OrderInfoUseCases
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orderModule = module {

    // LoyaltyCustomerRepository
    singleOf(::LoyaltyCustomerRepositoryImpl) { bind<LoyaltyCustomerRepository>() }

    // OrderRepository
    singleOf(::OrderRepositoryImpl) { bind<OrderRepository>() }

    // UseCases
    singleOf(::CreateOrderUseCaseImpl) { bind<CreateOrderUseCase>() }
    singleOf(::CalculateCartTotalWithDiscountUseCaseImpl) { bind<CalculateCartTotalWithDiscountUseCase>() }
    singleOf(::ResolvePickupPointUseCaseImpl) { bind<ResolvePickupPointUseCase>() }
    singleOf(::ApplyPhoneDiscountUseCaseImpl) { bind<ApplyPhoneDiscountUseCase>() }
    singleOf(::PickupOnlyRemoveUseCaseImpl) { bind<PickupOnlyRemoveUseCase>() }

    // Группы юзкейсов для OrderViewModel
    singleOf(::CartContentUseCases)
    singleOf(::OrderInfoUseCases)
    singleOf(::AddressUseCases)

    // ViewModel
    singleOf(::OrderViewModel)

    // Helper для OrderViewModel
    singleOf(::OrderCreator)
}





