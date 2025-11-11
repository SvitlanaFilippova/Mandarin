package com.mandarinkafe.mandarin.features.account.di

import com.mandarinkafe.mandarin.features.account.data.impl.UserInfoRepositoryImpl
import com.mandarinkafe.mandarin.features.account.domain.api.UserInfoRepository
import com.mandarinkafe.mandarin.features.account.presentation.viewmodel.AccountViewModel
import com.mandarinkafe.mandarin.features.auth.domain.api.DeleteAccountUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.GetActiveSessionsUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RevokeSessionUseCase
import com.mandarinkafe.mandarin.features.auth.domain.impl.DeleteAccountUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.GetActiveSessionsUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.RevokeSessionUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val accountModule = module {
    // Data Layer
    singleOf(::UserInfoRepositoryImpl) { bind<UserInfoRepository>() }

    // Domain Layer
    singleOf(::GetActiveSessionsUseCaseImpl) { bind<GetActiveSessionsUseCase>() }
    singleOf(::RevokeSessionUseCaseImpl) { bind<RevokeSessionUseCase>() }
    singleOf(::DeleteAccountUseCaseImpl) { bind<DeleteAccountUseCase>() }
    // Use cases для верификации телефона берутся из AuthModule

    // ViewModel
    singleOf(::AccountViewModel)
}