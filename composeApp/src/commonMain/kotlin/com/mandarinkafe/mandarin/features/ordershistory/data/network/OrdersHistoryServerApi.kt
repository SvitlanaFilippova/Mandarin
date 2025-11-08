package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.core.data.dto.Response
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
import kotlinx.serialization.json.Json

class OrdersHistoryServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getOrdersHistory(token: String): OrdersHistoryResponse {
        return try {
            val httpResponse = client.get("/orders/history") {
                header("x-api-key", key)
                header("Authorization", token)
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
                    Napier.e("OrdersHistoryServerApi: getOrdersHistory - HTTP error ${httpResponse.status.value}")
                    OrdersHistoryResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("OrdersHistoryServerApi: getOrdersHistory - Exception", e)
            OrdersHistoryResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun createOrUpdateOrder(token: String, body: OrdersHistoryUpdateRequest): Response {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val requestBodyJson = json.encodeToString(OrdersHistoryUpdateRequest.serializer(), body)
            Napier.d("SAVE_ORDER DEBUG: Sending POST request to /orders/history, orderId=${body.data.id}")
            Napier.d("SAVE_ORDER DEBUG: Request body: $requestBodyJson")
            
            val httpResponse = client.post("/orders/history") {
                header("x-api-key", key)
                header("Authorization", token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Napier.d("SAVE_ORDER SUCCESS: Server returned OK, orderId=${body.data.id}")
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Napier.e("SAVE_ORDER ERROR: Unauthorized, orderId=${body.data.id}")
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                HttpStatusCode.UnprocessableEntity -> {
                    val errorBody = getErrorBody(httpResponse)
                    Napier.e("SAVE_ORDER ERROR: HTTP 422 Unprocessable Entity, orderId=${body.data.id}")
                    Napier.e("SAVE_ORDER ERROR: Error body: $errorBody")
                    Response().apply { resultCode = HttpStatusCode.UnprocessableEntity.value }
                }

                else -> {
                    val errorBody = getErrorBody(httpResponse)
                    Napier.e("SAVE_ORDER ERROR: HTTP error ${httpResponse.status.value}, orderId=${body.data.id}")
                    Napier.e("SAVE_ORDER ERROR: Error body: $errorBody")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("SAVE_ORDER ERROR: Exception in HTTP request, orderId=${body.data.id}", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private suspend fun getErrorBody(httpResponse: HttpResponse): String {
        return try {
            httpResponse.body<String>()
        } catch (e: Exception) {
            "Failed to read error body: ${e.message}"
        }
    }

    suspend fun deleteOrder(token: String, orderId: String): Response {
        return try {
            val httpResponse = client.delete("/orders/history/$orderId") {
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
                    Napier.e("OrdersHistoryServerApi: deleteOrder - HTTP error ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("OrdersHistoryServerApi: deleteOrder - Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}

