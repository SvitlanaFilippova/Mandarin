package com.mandarinkafe.mandarin.features.ordershistory.data.network

import com.mandarinkafe.mandarin.BuildKonfig
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.NetworkMonitor
import com.mandarinkafe.mandarin.features.order.data.network.ServerAddPaymentRequest
import com.mandarinkafe.mandarin.features.order.data.network.ServerCancelOrderRequest
import com.mandarinkafe.mandarin.features.order.data.network.ServerCreateOrderRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.CreateDeliveryResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
import com.mandarinkafe.mandarin.util.Constants.HEADER_API_KEY
import com.mandarinkafe.mandarin.util.Constants.HEADER_AUTHORIZATION
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class OrdersHistoryServerApi(
    private val client: HttpClient,
    private val networkMonitor: NetworkMonitor,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    suspend fun createOrder(token: String, body: ServerCreateOrderRequest): CreateDeliveryResponse {
        if (!isConnected()) {
            return CreateDeliveryResponse(correlationId = "").apply { resultCode = NO_CONNECTION }
        }

        return try {
            val httpResponse = client.post("/orders/create") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: CreateDeliveryResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    CreateDeliveryResponse(correlationId = "")
                        .apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                HttpStatusCode.BadRequest, HttpStatusCode.UnprocessableEntity -> {
                    val errorBody = httpResponse.bodyAsText()
                    Napier.e("createOrder validation error: $errorBody")
                    CreateDeliveryResponse(correlationId = "")
                        .apply { resultCode = httpResponse.status.value }
                }

                else -> {
                    CreateDeliveryResponse(correlationId = "").apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("createOrder error: $e")
            CreateDeliveryResponse(correlationId = "").apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getOrderStatus(token: String, orderId: String): OrdersInfoResponse {
        if (!isConnected()) {
            return OrdersInfoResponse(correlationId = "").apply { resultCode = NO_CONNECTION }
        }

        return try {
            val httpResponse = client.get("/orders/status/$orderId") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: OrdersInfoResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.NotFound -> {
                    OrdersInfoResponse(correlationId = "")
                        .apply { resultCode = HttpStatusCode.NotFound.value }
                }

                HttpStatusCode.Unauthorized -> {
                    OrdersInfoResponse(correlationId = "")
                        .apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    OrdersInfoResponse(correlationId = "").apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("getOrderStatus error: $e")
            OrdersInfoResponse(correlationId = "").apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun cancelOrder(
        token: String,
        orderId: String,
        body: ServerCancelOrderRequest,
    ): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val httpResponse = client.post("/orders/$orderId/cancel") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> Response().apply { resultCode = HTTP_SUCCESS }
                HttpStatusCode.Unauthorized -> Response().apply {
                    resultCode = HttpStatusCode.Unauthorized.value
                }

                HttpStatusCode.NotFound -> Response().apply {
                    resultCode = HttpStatusCode.NotFound.value
                }

                else -> Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        } catch (e: Throwable) {
            Napier.e("cancelOrder error: $e")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun addPayment(
        token: String,
        orderId: String,
        body: ServerAddPaymentRequest,
    ): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }

        return try {
            val httpResponse = client.post("/orders/$orderId/payments") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(body)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> Response().apply { resultCode = HTTP_SUCCESS }
                HttpStatusCode.Unauthorized -> Response().apply {
                    resultCode = HttpStatusCode.Unauthorized.value
                }

                HttpStatusCode.NotFound -> Response().apply {
                    resultCode = HttpStatusCode.NotFound.value
                }

                else -> Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        } catch (e: Throwable) {
            Napier.e("addPayment error: $e")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getOrdersHistory(token: String): OrdersHistoryResponse {
        if (!isConnected()) {
            return OrdersHistoryResponse().apply { resultCode = NO_CONNECTION }
        }

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
                    OrderHistoryItemResponse().apply {
                        resultCode = HttpStatusCode.Unauthorized.value
                    }
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

    suspend fun getOrderDetails(token: String, orderId: String): OrderDetailsResponse {
        return try {
            val httpResponse = client.get("/orders/history/$orderId/details") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val rawBody = httpResponse.bodyAsText()
                    val responseBody: OrderDetailsResponse = json.decodeFromString(rawBody)
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.NotFound -> {
                    OrderDetailsResponse(
                        id = orderId,
                        timestamp = 0L
                    ).apply { resultCode = HttpStatusCode.NotFound.value }
                }

                HttpStatusCode.Unauthorized -> {
                    OrderDetailsResponse(
                        id = orderId,
                        timestamp = 0L
                    ).apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    OrderDetailsResponse(
                        id = orderId,
                        timestamp = 0L
                    ).apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("getOrderDetails error: $e")
            OrderDetailsResponse(
                id = orderId,
                timestamp = 0L
            ).apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun changePaymentMethod(
        token: String,
        orderId: String,
        paymentMethodCode: String,
    ): Response {
        return try {
            val request = ChangePaymentMethodRequest(paymentMethodCode = paymentMethodCode)
            val httpResponse = client.patch("/orders/history/$orderId/payment-method") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    Response().apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    Response().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                HttpStatusCode.NotFound -> {
                    Response().apply { resultCode = HttpStatusCode.NotFound.value }
                }

                else -> {
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("changePaymentMethod error: $e")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}
