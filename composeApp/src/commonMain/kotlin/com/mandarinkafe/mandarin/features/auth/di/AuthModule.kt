package com.mandarinkafe.mandarin.features.auth.di

import com.mandarinkafe.mandarin.features.auth.data.impl.AuthRepositoryImpl
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.CheckVerificationStatusUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.impl.CheckVerificationStatusUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.RequestPhoneVerificationUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authModule = module {
    // Data Layer
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }

    // Domain Layer
    singleOf(::RequestPhoneVerificationUseCaseImpl) { bind<RequestPhoneVerificationUseCase>() }
    singleOf(::CheckVerificationStatusUseCaseImpl) { bind<CheckVerificationStatusUseCase>() }
    // ViewModel
    singleOf(::AuthViewModel)
}

