package com.mandarinkafe.mandarin.core.di

import android.util.Log
import com.mandarinkafe.mandarin.core.data.network.api.IikoApi
import com.mandarinkafe.mandarin.core.data.network.auth.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.auth.IikoAuthProvider
import com.mandarinkafe.mandarin.util.Constants.IIKO_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
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
    // Клиент для аутентификации
    @Provides
    @Singleton
    @IikoAuthClient
    fun provideAuthHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) { level = LogLevel.HEADERS }

            defaultRequest {
                url(IIKO_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }


    // API для аутентификации (не требует токена)
    @Provides
    @Singleton
    fun provideIikoAuthApi(
        @IikoAuthClient client: HttpClient
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

    // Основной клиент с токеном
    @Provides
    @Singleton
    @IikoClient
    fun provideHttpClient(
        authProvider: IikoAuthProvider
    ): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                    explicitNulls = false
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorDebug", message)
                    }
                }
                level = LogLevel.ALL
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = authProvider.getToken(),
                            refreshToken = authProvider.getToken()
                        )
                    }
                    refreshTokens {
                        BearerTokens(
                            accessToken = authProvider.refreshToken(),
                            refreshToken = authProvider.refreshToken()
                        )
                    }
                }
            }

            defaultRequest {
                url(IIKO_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // Основной API класс с методами, требующими аутентификации
    @Provides
    @Singleton
    fun provideIikoApi(
        @IikoClient client: HttpClient,
    ): IikoApi {
        return IikoApi(client)
    }
}