package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.core.data.network.AuthInterceptor
import com.mandarinkafe.mandarin.core.data.network.api.IikoApi
import com.mandarinkafe.mandarin.core.data.network.api.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.auth.IikoAuthProvider
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IikoApiModule {
    @Provides
    @Singleton
    @IikoClient
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }

            defaultRequest {
                url(IIKO_BASE_URL)
                contentType(ContentType.Application.Json)
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
    }

    // API для аутентификации (не требует токена)
    @Provides
    @Singleton
    fun provideIikoAuthApi(
        @IikoClient client: HttpClient
    ): IikoAuthApi {
        return IikoAuthApi(client)
    }

    // AuthProvider для управления токенами
    @Provides
    @Singleton
    fun provideIikoAuthProvider(
        authApi: IikoAuthApi
    ): IikoAuthProvider {
        return IikoAuthProvider(authApi)
    }

    // AuthInterceptor для добавления токена к запросам
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authProvider: IikoAuthProvider
    ): AuthInterceptor {
        return AuthInterceptor(authProvider)
    }

    // Основной API класс с методами, требующими аутентификации
    @Provides
    @Singleton
    fun provideIikoApi(
        @IikoClient client: HttpClient,
        authInterceptor: AuthInterceptor
    ): IikoApi {
        return IikoApi(client, authInterceptor)
    }
}