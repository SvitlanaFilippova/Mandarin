package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.shared.BuildKonfig
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
import io.ktor.http.HttpStatusCode

class OrdersHistoryServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getOrdersHistory(token: String): OrdersHistoryResponse {
        return try {
            val httpResponse = client.get("/orders/history") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: OrdersHistoryResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    OrdersHistoryResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    OrdersHistoryResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("getOrdersHistory error: $e")
            OrdersHistoryResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun createOrUpdateOrder(token: String, body: OrdersHistoryUpdateRequest): Response {
        return try {
            val httpResponse = client.post("/orders/history") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                HttpStatusCode.UnprocessableEntity -> {
                    Response().apply { resultCode = HttpStatusCode.UnprocessableEntity.value }
                }

                else -> {
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("createOrUpdateOrder error: $e")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getOrderById(token: String, orderId: String): OrderHistoryItemResponse {
        return try {
            val httpResponse = client.get("/orders/history/$orderId") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: OrderHistoryItemResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.NotFound -> {
                    OrderHistoryItemResponse().apply { resultCode = HttpStatusCode.NotFound.value }
                }

                HttpStatusCode.Unauthorized -> {
                    OrderHistoryItemResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    OrderHistoryItemResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("getOrderById error: $e")
            OrderHistoryItemResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun deleteOrder(token: String, orderId: String): Response {
        return try {
            val httpResponse = client.delete("/orders/history/$orderId") {
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
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("deleteOrder error: $e")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}

