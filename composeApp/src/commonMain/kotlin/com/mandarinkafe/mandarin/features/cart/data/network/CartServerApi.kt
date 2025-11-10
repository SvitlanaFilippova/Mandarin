package com.mandarinkafe.mandarin.features.cart.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.cart.data.dto.CartItemDto
import com.mandarinkafe.mandarin.features.cart.data.dto.CartRequest
import com.mandarinkafe.mandarin.features.cart.data.dto.CartResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.ERROR_BODY_READ_FAILED
import com.mandarinkafe.mandarin.util.Constants.HEADER_API_KEY
import com.mandarinkafe.mandarin.util.Constants.HEADER_AUTHORIZATION
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class CartServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getCart(token: String): CartResponse {
        return try {
            val httpResponse = client.get("/cart") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
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
                    Napier.e("$LOG_TAG.getCart: HTTP error ${httpResponse.status.value}")
                    CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.getCart: Exception", e)
            CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun updateCart(token: String, items: List<CartItemDto>): CartResponse {
        return try {
            val request = CartRequest(items, lastUpdated = 0L)
            val httpResponse = client.post("/cart") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: CartResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    CartResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                HttpStatusCode.UnprocessableEntity -> {
                    val errorBody = try {
                        httpResponse.bodyAsText()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG.updateCart: HTTP 422 (Unprocessable Entity), тело ответа: $errorBody")
                    CartResponse().apply { resultCode = HttpStatusCode.UnprocessableEntity.value }
                }

                else -> {
                    val errorBody = try {
                        httpResponse.bodyAsText()
                    } catch (e: Exception) {
                        "$ERROR_BODY_READ_FAILED${e.message}"
                    }
                    Napier.e("$LOG_TAG.updateCart: HTTP error ${httpResponse.status.value}, тело ответа: $errorBody")
                    CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.updateCart: Exception", e)
            CartResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun clearCart(token: String): Response {
        return try {
            val httpResponse = client.delete("/cart") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("$LOG_TAG: clearCart - HTTP error ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG clearCart - Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private companion object {
        const val LOG_TAG = "CartServerApi"
    }
}

