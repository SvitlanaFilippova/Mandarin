package com.mandarinkafe.mandarin.features.more.di

import android.content.Context
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object MoreModule {

    @Provides
    fun provideTelegramApi(): TelegramApi {
        return Retrofit.Builder()
            .baseUrl(TELEGRAM_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TelegramApi::class.java)
    }

    @Provides
    fun provideFeedbackRepository(
        api: TelegramApi
    ): FeedbackRepository = FeedbackRepositoryImpl(telegramApi = api)

    @Provides
    fun provideDevFeedbackRepository(
        api: TelegramApi,
        deviceInfoProvider: DeviceInfoProvider
    ): DevFeedbackRepository =
        DevFeedbackRepositoryImpl(telegramApi = api, deviceInfoProvider = deviceInfoProvider)

    @Provides
    fun provideDeviceInfoProvider(
        @ApplicationContext context: Context
    ): DeviceInfoProvider = DeviceInfoProviderImpl(context = context)
}