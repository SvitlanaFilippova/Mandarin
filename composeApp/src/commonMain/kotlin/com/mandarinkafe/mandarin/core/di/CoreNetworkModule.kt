package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.BuildKonfig
import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.core.data.network.impl.ServerNetworkClientImpl
import com.mandarinkafe.mandarin.features.auth.data.network.PublicAuthApi
import com.mandarinkafe.mandarin.features.auth.data.network.ServerAuthApi
import com.mandarinkafe.mandarin.features.auth.data.network.ServerAuthProvider
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreNetworkModule = module {

    // HttpClient для публичных запросов к Server API (без интерсептора)
    single(named(DiConstants.SERVER_CLIENT_QUALIFIER)) {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
            defaultRequest {
                url(BuildKonfig.SERVER_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // ServerAuthProvider
    single {
        ServerAuthProvider(
            tokenStorage = get(),
            refreshTokenClient = get(named(DiConstants.SERVER_CLIENT_QUALIFIER))
        )
    }

    // HttpClient для авторизованных запросов к Server API с автоматическим обновлением токенов
    single(named(DiConstants.SERVER_AUTH_CLIENT_QUALIFIER)) {
        val authProvider: ServerAuthProvider = get()
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        try {
                            val accessToken = authProvider.getToken()
                            val tokens = BearerTokens(
                                accessToken = accessToken,
                                refreshToken = accessToken // Используем access token как refresh для совместимости
                            )
                            tokens
                        } catch (e: Exception) {
                            Napier.e("AUTH_INTERCEPTOR ERROR: Failed to load tokens", e)
                            throw e
                        }
                    }
                    refreshTokens {
                        try {
                            val newAccessToken = authProvider.refreshToken()
                            val tokens = BearerTokens(
                                accessToken = newAccessToken,
                                refreshToken = newAccessToken // Используем новый access token
                            )
                            tokens
                        } catch (e: Exception) {
                            Napier.e("AUTH_INTERCEPTOR ERROR: Failed to refresh tokens", e)
                            throw e
                        }
                    }
                }
            }

            defaultRequest {
                url(BuildKonfig.SERVER_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // ServerApi
    single {
        ServerApi(get(named(DiConstants.SERVER_CLIENT_QUALIFIER)))
    }

    // PublicAuthApi (для публичных запросов, только API key)
    single {
        PublicAuthApi(get(named(DiConstants.SERVER_CLIENT_QUALIFIER)))
    }

    // AuthApi (для авторизованных запросов, использует авторизованный клиент с автоматическим обновлением токенов)
    single {
        ServerAuthApi(get(named(DiConstants.SERVER_AUTH_CLIENT_QUALIFIER)))
    }

    // Network Clients
    singleOf(::ServerNetworkClientImpl) { bind<ServerNetworkClient>() }

}
