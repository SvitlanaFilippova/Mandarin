package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.network.api.ServerMenuApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerApiModule {

    private const val SERVER_BASE_URL = BuildConfig.SERVER_BASE_URL

    @Provides
    @Singleton
    fun provideServerMenuApi(): ServerMenuApi {
        return Retrofit.Builder()
            .baseUrl(SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerMenuApi::class.java)
    }

}
