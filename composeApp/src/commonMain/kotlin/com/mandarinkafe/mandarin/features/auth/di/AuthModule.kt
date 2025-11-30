package com.mandarinkafe.mandarin.features.auth.di

import com.mandarinkafe.mandarin.features.auth.data.api.LocalUserDataCleaner
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorage
import com.mandarinkafe.mandarin.features.auth.data.datastore.TokenStorageImpl
import com.mandarinkafe.mandarin.features.auth.data.impl.AuthRepositoryImpl
import com.mandarinkafe.mandarin.features.auth.data.impl.LocalUserDataCleanerImpl
import com.mandarinkafe.mandarin.features.auth.data.impl.PhoneVerificationRepositoryImpl
import com.mandarinkafe.mandarin.features.auth.data.network.AuthNetworkClient
import com.mandarinkafe.mandarin.features.auth.data.network.AuthNetworkClientImpl
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.PhoneVerificationRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestPhoneVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.RequestSmsVerificationUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import com.mandarinkafe.mandarin.features.auth.domain.api.VerificationStatusInteractor
import com.mandarinkafe.mandarin.features.auth.domain.impl.RequestPhoneVerificationUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.RequestSmsVerificationUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.SyncUserDataUseCaseImpl
import com.mandarinkafe.mandarin.features.auth.domain.impl.UserSessionManager
import com.mandarinkafe.mandarin.features.auth.domain.impl.VerificationStatusInteractorImpl
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authModule = module {
    // Data Layer
    singleOf(::TokenStorageImpl) { bind<TokenStorage>() }
    single<AuthNetworkClient> {
        AuthNetworkClientImpl(
            publicApi = get(),
            authApi = get(),
            networkMonitor = get()
        )
    }
    singleOf(::LocalUserDataCleanerImpl) { bind<LocalUserDataCleaner>() }
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::PhoneVerificationRepositoryImpl) { bind<PhoneVerificationRepository>() }

    // Domain Layer
    singleOf(::RequestPhoneVerificationUseCaseImpl) { bind<RequestPhoneVerificationUseCase>() }
    singleOf(::VerificationStatusInteractorImpl) { bind<VerificationStatusInteractor>() }
    singleOf(::RequestSmsVerificationUseCaseImpl) { bind<RequestSmsVerificationUseCase>() }
    singleOf(::SyncUserDataUseCaseImpl) { bind<SyncUserDataUseCase>() }
    singleOf(::UserSessionManager)

    // ViewModel
    singleOf(::AuthViewModel)
}

