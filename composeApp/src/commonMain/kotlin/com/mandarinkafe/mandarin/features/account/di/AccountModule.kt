package com.mandarinkafe.mandarin.features.account.di

import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountViewModel
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
import com.mandarinkafe.mandarin.features.auth.domain.impl.GetActiveSessionsUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.RevokeSessionUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val accountModule = module {
    // Data Layer
    // (используется AuthRepository из authModule)

    // Domain Layer
    singleOf(::GetActiveSessionsUseCaseImpl) { bind<GetActiveSessionsUseCase>() }
    singleOf(::RevokeSessionUseCaseImpl) { bind<RevokeSessionUseCase>() }

    // ViewModel
    singleOf(::AccountViewModel)
}