package com.mandarinkafe.mandarin.features.more.di

import com.mandarinkafe.mandarin.core.di.DiConstants.SERVER_AUTH_CLIENT_QUALIFIER
import com.mandarinkafe.mandarin.features.delivery.presentation.viewmodel.DeliveryViewModel
import com.mandarinkafe.mandarin.features.more.data.impl.AppStoresRepositoryImpl
import com.mandarinkafe.mandarin.features.more.data.impl.FeedbackRepositoryImpl
import com.mandarinkafe.mandarin.features.more.data.network.FeedbackServerApi
import com.mandarinkafe.mandarin.features.more.domain.api.AppStoresRepository
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.api.GetAppStoresUseCase
import com.mandarinkafe.mandarin.features.more.domain.impl.GetAppStoresUseCaseImpl
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.AboutViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val moreModule = module {

    // --- API ---
    single { FeedbackServerApi(get(named(SERVER_AUTH_CLIENT_QUALIFIER))) }

    // --- Repositories ---
    singleOf(::FeedbackRepositoryImpl) { bind<FeedbackRepository>() }
    singleOf(::AppStoresRepositoryImpl) { bind<AppStoresRepository>() }

    // --- Use Cases ---
    singleOf(::GetAppStoresUseCaseImpl) { bind<GetAppStoresUseCase>() }

    // --- ViewModels ---
    singleOf(::AboutViewModel)
    singleOf(::DeliveryViewModel)
    singleOf(::FeedbackViewModel)
}
