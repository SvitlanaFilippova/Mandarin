package com.mandarinkafe.mandarin.features.cart.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.cart.data.dto.CartItemDto
import com.mandarinkafe.mandarin.features.cart.data.dto.CartResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class CartServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getCart(token: String): CartResponse {
        return try {
            val httpResponse = client.get("/cart") {
                header("x-api-key", key)
                header("Authorization", token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: CartResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    CartResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("CartServerApi: getCart - HTTP error ${httpResponse.status.value}")
                    CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("CartServerApi: getCart - Exception", e)
            CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun updateCart(token: String, items: List<CartItemDto>): CartResponse {
        return try {
            val httpResponse = client.post("/cart") {
                header("x-api-key", key)
                header("Authorization", token)
                setBody(items)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: CartResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    CartResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("CartServerApi: updateCart - HTTP error ${httpResponse.status.value}")
                    CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("CartServerApi: updateCart - Exception", e)
            CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun clearCart(token: String): Response {
        return try {
            val httpResponse = client.delete("/cart") {
                header("x-api-key", key)
                header("Authorization", token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("CartServerApi: clearCart - HTTP error ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("CartServerApi: clearCart - Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}

