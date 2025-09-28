package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.network.AuthInterceptor
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthSyncApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoDiscountApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoOrderApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoTerminalApi
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IikoApiModule {

    @Provides
    @Singleton
    fun provideAuthSyncApi(): IikoAuthSyncApi {
        return Retrofit.Builder()
            .baseUrl(IIKO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IikoAuthSyncApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authSyncApi: IikoAuthSyncApi): AuthInterceptor {
        return AuthInterceptor(authSyncApi)
    }

    @Provides
    @Singleton
    fun provideRetrofitWithAuth(authInterceptor: AuthInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(IIKO_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideIikoAuthApi(retrofit: Retrofit): IikoAuthApi =
        retrofit.create(IikoAuthApi::class.java)

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