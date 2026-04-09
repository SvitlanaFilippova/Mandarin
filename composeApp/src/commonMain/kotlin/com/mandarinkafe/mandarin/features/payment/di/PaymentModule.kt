package com.mandarinkafe.mandarin.features.payment.di

import com.mandarinkafe.mandarin.core.di.DiConstants
import com.mandarinkafe.mandarin.features.payment.data.impl.PaymentRepositoryImpl
import com.mandarinkafe.mandarin.features.payment.data.network.PaymentNetworkClient
import com.mandarinkafe.mandarin.features.payment.data.network.PaymentNetworkClientImpl
import com.mandarinkafe.mandarin.features.payment.data.network.PaymentServerApi
import com.mandarinkafe.mandarin.features.payment.domain.api.CancelPaymentUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.CreatePaymentUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.GetPaymentStatusUseCase
import com.mandarinkafe.mandarin.features.payment.domain.api.PaymentRepository
import com.mandarinkafe.mandarin.features.payment.domain.impl.CancelPaymentUseCaseImpl
import com.mandarinkafe.mandarin.features.payment.domain.impl.CreatePaymentUseCaseImpl
import com.mandarinkafe.mandarin.features.payment.domain.impl.GetPaymentStatusUseCaseImpl
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val paymentModule = module {
    // API
    single {
        PaymentServerApi(
            client = get(named(DiConstants.SERVER_CLIENT_QUALIFIER))
        )
    }

    // Network Client
    singleOf(::PaymentNetworkClientImpl) { bind<PaymentNetworkClient>() }

    // Repository
    singleOf(::PaymentRepositoryImpl) { bind<PaymentRepository>() }

    // Use Cases
    singleOf(::CreatePaymentUseCaseImpl) { bind<CreatePaymentUseCase>() }
    singleOf(::GetPaymentStatusUseCaseImpl) { bind<GetPaymentStatusUseCase>() }
    singleOf(::CancelPaymentUseCaseImpl) { bind<CancelPaymentUseCase>() }

    // ViewModel — явный вызов конструктора, чтобы в DEX была стабильная ссылка на класс (Koin factoryOf + R8)
    factory {
        PaymentViewModel(
            yooKassaService = get(),
            createPaymentUseCase = get(),
            getPaymentStatusUseCase = get(),
            cancelPaymentUseCase = get(),
        )
    }

    // YooKassa Service регистрируется в platform-specific модулях
    // см. PaymentPlatformModule.kt в androidMain и iosMain
}

