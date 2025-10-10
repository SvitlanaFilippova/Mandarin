package com.mandarinkafe.mandarin.features.more.di

import com.mandarinkafe.mandarin.core.di.DiConstants.TELEGRAM_API_BASE_URL
import com.mandarinkafe.mandarin.core.di.DiConstants.TELEGRAM_CLIENT_QUALIFIER
import com.mandarinkafe.mandarin.features.more.data.DeviceInfoProvider
import com.mandarinkafe.mandarin.features.more.data.impl.DevFeedbackRepositoryImpl
import com.mandarinkafe.mandarin.features.more.data.impl.DeviceInfoProviderImpl
import com.mandarinkafe.mandarin.features.more.data.impl.FeedbackRepositoryImpl
import com.mandarinkafe.mandarin.features.more.data.network.TelegramApi
import com.mandarinkafe.mandarin.features.more.domain.api.DevFeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.AboutViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val moreModule = module {

    // --- HttpClient для Telegram ---
    single(named(TELEGRAM_CLIENT_QUALIFIER)) {
        HttpClient {
            defaultRequest {
                url(TELEGRAM_API_BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
    }

    // --- API ---
    single { TelegramApi(get(named("TelegramClient"))) }

    // --- Device info provider ---
    singleOf(::DeviceInfoProviderImpl) { bind<DeviceInfoProvider>() }

    // --- Repositories ---
    singleOf(::FeedbackRepositoryImpl) { bind<FeedbackRepository>() }
    singleOf(::DevFeedbackRepositoryImpl) { bind<DevFeedbackRepository>() }

    // ViewModels
    viewModelOf(::FeedbackViewModel)
    viewModelOf(::DevFeedbackViewModel)
    viewModelOf(::AboutViewModel)
    viewModelOf(::DeliveryViewModel)
}
