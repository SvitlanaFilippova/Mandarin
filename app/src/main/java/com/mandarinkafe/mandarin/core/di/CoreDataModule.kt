package com.mandarinkafe.mandarin.core.di

import android.content.Context
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsApiService
import com.mandarinkafe.mandarin.core.data.network.IikoApiService
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.core.data.network.RetrofitNetworkClient
import com.mandarinkafe.mandarin.util.Constants.GOOGLE_DOCS_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreDataModule {

    @Provides
    @Singleton
    fun provideIikoApiService(): IikoApiService {
        return Retrofit.Builder()
            .baseUrl(IIKO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(
                IikoApiService::
                class.java
            )
    }

    @Provides
    @Singleton
    fun provideGoogleDocsApiService(): GoogleDocsApiService {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_DOCS_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GoogleDocsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitNetworkClient(
        @ApplicationContext
        context: Context,
        ikkoService: IikoApiService,
        googleDocsApi: GoogleDocsApiService
    ): NetworkClient {
        return RetrofitNetworkClient(
            context = context, iikoService = ikkoService,
            googleDocsApi = googleDocsApi
        )
    }
}