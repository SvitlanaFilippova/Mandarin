package com.mandarinkafe.mandarin.core.di

import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsApiService
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.data.network.api.IikoApi
import com.mandarinkafe.mandarin.core.data.network.api.ServerApi
import com.mandarinkafe.mandarin.core.data.network.auth.IikoAuthApi
import com.mandarinkafe.mandarin.core.data.network.auth.IikoAuthProvider
import com.mandarinkafe.mandarin.core.data.network.impl.GoogleDocsNetworkClientImpl
import com.mandarinkafe.mandarin.core.data.network.impl.IikoNetworkClientImpl
import io.github.aakira.napier.Napier
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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreNetworkModule = module {

    // HttpClient для аутентификации (без токена)
    single(named(DiConstants.IIKO_AUTH_CLIENT_QUALIFIER)) {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) {
                level = LogLevel.NONE
            }
            defaultRequest {
                url(DiConstants.IIKO_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // Основной HttpClient с токеном
    single(named(DiConstants.IIKO_CLIENT_QUALIFIER)) {
        val authProvider: IikoAuthProvider = get()
        HttpClient {
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
                        Napier.d(message)
                    }
                }
                level = LogLevel.NONE
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
                url(DiConstants.IIKO_BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // HttpClient для Server API
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


    // HttpClient (GoogleDocs)
    single(named(DiConstants.GOOGLE_DOCS_CLIENT_QUALIFIER)) {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                contentType(ContentType.Text.Plain)
            }
        }
    }


    // Network Clients
    single<IikoNetworkClient> {
        IikoNetworkClientImpl(
            menuApi = get(),
            iikoApi = get(),
            networkMonitor = get()
        )
    }

    single<GoogleDocsNetworkClient> {
        GoogleDocsNetworkClientImpl(
            googleDocsApi = get(),
            networkMonitor = get()
        )
    }

    // IikoApi
    single {
        IikoApi(get(named(DiConstants.IIKO_CLIENT_QUALIFIER)))
    }

    // IikoAuthApi
    single {
        IikoAuthApi(get(named(DiConstants.IIKO_AUTH_CLIENT_QUALIFIER)))
    }

    // ServerApi
    single {
        ServerApi(get(named(DiConstants.SERVER_CLIENT_QUALIFIER)))
    }

    // GoogleDocsApiService
    single {
        GoogleDocsApiService(get(named(DiConstants.GOOGLE_DOCS_CLIENT_QUALIFIER)))
    }

    // IikoAuthProvider
    singleOf(::IikoAuthProvider)

    // Network Clients
    singleOf(::IikoNetworkClientImpl) { bind<IikoNetworkClient>() }
    singleOf(::GoogleDocsNetworkClientImpl) { bind<GoogleDocsNetworkClient>() }

}
