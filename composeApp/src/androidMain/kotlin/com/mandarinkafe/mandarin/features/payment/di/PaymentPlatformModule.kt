package com.mandarinkafe.mandarin.features.payment.di

import com.mandarinkafe.mandarin.features.payment.YooKassaPaymentService
import org.koin.dsl.module

val paymentPlatformModule = module {
    // YooKassa Service (Android actual implementation)
    single { YooKassaPaymentService() }
}

