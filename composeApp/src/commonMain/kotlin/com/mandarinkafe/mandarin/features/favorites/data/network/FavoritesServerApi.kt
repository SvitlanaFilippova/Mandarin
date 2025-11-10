package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class FavoritesServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun getFavorites(token: String): RemoteFavoritesResponse {
        return try {
            val httpResponse = client.get("/favorites") {
                header("x-api-key", key)
                header("Authorization", token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    // Читаем тело как строку для десериализации
                    val rawBody = httpResponse.bodyAsText()

                    try {
                        // Десериализуем из строки
                        val responseBody: RemoteFavoritesResponse = json.decodeFromString(rawBody)
                        responseBody.apply { resultCode = HTTP_SUCCESS }
                    } catch (e: Throwable) {
                        Napier.e("$LOG_TAG.getFavorites: ошибка десериализации ответа: ${e.message}")
                        throw e // Пробрасываем исключение дальше
                    }
                }

                HttpStatusCode.Unauthorized -> {
                    RemoteFavoritesResponse().apply {
                        resultCode = HttpStatusCode.Unauthorized.value
                    }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.bodyAsText()
                    } catch (e: Exception) {
                        "не удалось прочитать тело ответа: ${e.message}"
                    }
                    Napier.e("$LOG_TAG.getFavorites: HTTP error ${httpResponse.status.value}, тело ответа: $errorBody")
                    RemoteFavoritesResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.getFavorites: Exception: ${e.message}", e)
            RemoteFavoritesResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun updateFavorites(
        token: String,
        body: RemoteFavoritesUpdateRequest,
    ): RemoteFavoritesResponse {
        return try {
            val httpResponse = client.post("/favorites") {
                header("x-api-key", key)
                header("Authorization", token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: RemoteFavoritesResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    RemoteFavoritesResponse().apply {
                        resultCode = HttpStatusCode.Unauthorized.value
                    }
                }

                HttpStatusCode.UnprocessableEntity -> {
                    val errorBody = try {
                        httpResponse.bodyAsText()
                    } catch (e: Exception) {
                        "не удалось прочитать тело ответа: ${e.message}"
                    }
                    Napier.e("$LOG_TAG.updateFavorites: HTTP 422 (Unprocessable Entity), тело ответа: $errorBody")
                    RemoteFavoritesResponse().apply {
                        resultCode = HttpStatusCode.UnprocessableEntity.value
                    }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.bodyAsText()
                    } catch (e: Exception) {
                        "не удалось прочитать тело ответа: ${e.message}"
                    }
                    Napier.e("$LOG_TAG.updateFavorites: HTTP error ${httpResponse.status.value}, тело ответа: $errorBody")
                    RemoteFavoritesResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.updateFavorites: ${e.message}", e)
            RemoteFavoritesResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private companion object {
        const val LOG_TAG = "FavoritesServerApi"
    }
}