package com.mandarinkafe.mandarin.features.infrastructure.di

import android.content.Context
import android.content.SharedPreferences
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mandarinkafe.mandarin.core.data.MenuRefreshOnResumeObserver
import com.mandarinkafe.mandarin.core.di.DiConstants
import com.mandarinkafe.mandarin.database.AppDatabase
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
import com.mandarinkafe.mandarin.util.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val infrastructureModule = module {
    // Database
    single {
        val driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = androidContext(),
            name = DiConstants.DATABASE_NAME
        )
        AppDatabase(driver)
    }

    // SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences(DiConstants.LOCAL_STORAGE_NAME, Context.MODE_PRIVATE)
    }

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

    singleOf(::MenuRefreshOnResumeObserver)
    singleOf(::NetworkMonitor)

}
