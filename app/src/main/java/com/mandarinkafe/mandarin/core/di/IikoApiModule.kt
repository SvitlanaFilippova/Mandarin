package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoDiscountApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoMenuApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoOrderApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoTerminalApi
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IikoApiModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(IIKO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideIikoAuthApi(retrofit: Retrofit): IikoAuthApi =
        retrofit.create(IikoAuthApi::class.java)

    @Provides
    @Singleton
    fun provideIikoMenuApi(retrofit: Retrofit): IikoMenuApi =
        retrofit.create(IikoMenuApi::class.java)

    @Provides
    @Singleton
    fun provideIikoOrderApi(retrofit: Retrofit): IikoOrderApi =
        retrofit.create(IikoOrderApi::class.java)

    @Provides
    @Singleton
    fun provideIikoDiscountApi(retrofit: Retrofit): IikoDiscountApi =
        retrofit.create(IikoDiscountApi::class.java)

    @Provides
    @Singleton
    fun provideIikoTerminalApi(retrofit: Retrofit): IikoTerminalApi =
        retrofit.create(IikoTerminalApi::class.java)
}
