package com.mandarinkafe.mandarin.features.more.di

import android.content.Context
import com.mandarinkafe.mandarin.core.di.TelegramClient
import com.mandarinkafe.mandarin.features.more.data.DeviceInfoProvider
import com.mandarinkafe.mandarin.features.more.data.impl.DevFeedbackRepositoryImpl
import com.mandarinkafe.mandarin.features.more.data.impl.DeviceInfoProviderImpl
import com.mandarinkafe.mandarin.features.more.data.impl.FeedbackRepositoryImpl
import com.mandarinkafe.mandarin.features.more.data.network.TelegramApi
import com.mandarinkafe.mandarin.features.more.domain.api.DevFeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.util.Constants.TELEGRAM_API_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoreModule {

    @Provides
    @Singleton
    @TelegramClient
    fun provideTelegramHttpClient(): HttpClient {
        return HttpClient {
            defaultRequest {
                url(TELEGRAM_API_BASE_URL)
                contentType(ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideTelegramApi(
        @TelegramClient client: HttpClient
    ): TelegramApi {
        return TelegramApi(client)
    }

    @Provides
    @Singleton
    fun provideFeedbackRepository(
        api: TelegramApi
    ): FeedbackRepository = FeedbackRepositoryImpl(telegramApi = api)

    @Provides
    @Singleton
    fun provideDevFeedbackRepository(
        api: TelegramApi,
        deviceInfoProvider: DeviceInfoProvider
    ): DevFeedbackRepository =
        DevFeedbackRepositoryImpl(telegramApi = api, deviceInfoProvider = deviceInfoProvider)

    @Provides
    @Singleton
    fun provideDeviceInfoProvider(
        @ApplicationContext context: Context
    ): DeviceInfoProvider = DeviceInfoProviderImpl(context = context)
}