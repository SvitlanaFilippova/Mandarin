package com.mandarinkafe.mandarin.features.infrastructure.di

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
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.PaymentTypesRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.impl.CheckDiscountByPhoneUseCaseImpl
import com.mandarinkafe.mandarin.features.infrastructure.domain.impl.CheckIfTerminalIsAliveUseCaseImpl
import com.mandarinkafe.mandarin.features.infrastructure.domain.impl.GetPaymentTypesUseCaseImpl
import com.mandarinkafe.mandarin.database.AppDatabase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val infrastructureModule = module {
    // PaymentTypes
    singleOf(::PaymentTypesRepositoryImpl) { bind<PaymentTypesRepository>() }
    singleOf(::GetPaymentTypesUseCaseImpl) { bind<GetPaymentTypesUseCase>() }

    // AliveTerminal
    singleOf(::AliveTerminalRepositoryImpl) { bind<AliveTerminalRepository>() }
    singleOf(::CheckIfTerminalIsAliveUseCaseImpl) { bind<CheckIfTerminalIsAliveUseCase>() }

    // CategoryDiscounts
    single { get<AppDatabase>().categoryDiscountQueries }
    singleOf(::SQLDelightCategoryDiscountsStorage) { bind<CategoryDiscountsStorage>() }
    singleOf(::CategoryDiscountRepositoryImpl) { bind<CategoryDiscountRepository>() }

    // CheckDiscountByPhone
    singleOf(::CheckDiscountByPhoneUseCaseImpl) { bind<CheckDiscountByPhoneUseCase>() }

}