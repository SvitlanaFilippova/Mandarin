package com.mandarinkafe.mandarin.features.favorites.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto
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
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class FavoritesServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getFavorites(token: String): RemoteFavoritesResponse {
        return try {
            val httpResponse = client.get("/favorites") {
                header("x-api-key", key)
                header("Authorization", token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: RemoteFavoritesResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    RemoteFavoritesResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("FavoritesServerApi: getFavorites - HTTP error ${httpResponse.status.value}")
                    RemoteFavoritesResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("FavoritesServerApi: getFavorites - Exception", e)
            RemoteFavoritesResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun updateFavorites(token: String, body: RemoteFavoritesUpdateRequest): Response {
        return try {
            val httpResponse = client.post("/favorites") {
                header("x-api-key", key)
                header("Authorization", token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("FavoritesServerApi: updateFavorites - HTTP error ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("FavoritesServerApi: updateFavorites - Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}