package com.mandarinkafe.mandarin.features.payment.data.network

import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentResponse
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentResponse
import com.mandarinkafe.mandarin.features.payment.data.dto.PaymentStatusResponse
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.ERROR_BODY_READ_FAILED
import com.mandarinkafe.mandarin.util.Constants.HEADER_API_KEY
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

class PaymentServerApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun createPayment(request: CreatePaymentRequest): CreatePaymentResponse {
        return try {
            val httpResponse = client.post("/api/payments/create") {
                header(HEADER_API_KEY, key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: CreatePaymentResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.BadRequest -> {
                    try {
                        val errorBody = httpResponse.bodyAsText()
                        Napier.e("$LOG_TAG.createPayment: BadRequest - $errorBody")
                    } catch (e: Exception) {
                        Napier.e("$LOG_TAG.createPayment: $ERROR_BODY_READ_FAILED${e.message}")
                    }
                    CreatePaymentResponse().apply { resultCode = HttpStatusCode.BadRequest.value }
                }

                else -> {
                    Napier.e("$LOG_TAG.createPayment: HTTP error ${httpResponse.status.value}")
                    CreatePaymentResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.createPayment: Exception", e)
            CreatePaymentResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun getPaymentStatus(orderId: String): PaymentStatusResponse {
        return try {
            val httpResponse = client.get("/api/payments/status/$orderId") {
                header(HEADER_API_KEY, key)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: PaymentStatusResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.NotFound -> {
                    PaymentStatusResponse().apply { resultCode = HttpStatusCode.NotFound.value }
                }

                else -> {
                    Napier.e("$LOG_TAG.getPaymentStatus: HTTP error ${httpResponse.status.value}")
                    PaymentStatusResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.getPaymentStatus: Exception", e)
            PaymentStatusResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun cancelPayment(request: CancelPaymentRequest): CancelPaymentResponse {
        return try {
            val httpResponse = client.post("/api/payments/cancel") {
                header(HEADER_API_KEY, key)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: CancelPaymentResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.BadRequest -> {
                    try {
                        val errorBody = httpResponse.bodyAsText()
                        Napier.e("$LOG_TAG.cancelPayment: BadRequest - $errorBody")
                    } catch (e: Exception) {
                        Napier.e("$LOG_TAG.cancelPayment: $ERROR_BODY_READ_FAILED${e.message}")
                    }
                    CancelPaymentResponse().apply { resultCode = HttpStatusCode.BadRequest.value }
                }

                else -> {
                    Napier.e("$LOG_TAG.cancelPayment: HTTP error ${httpResponse.status.value}")
                    CancelPaymentResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.cancelPayment: Exception", e)
            CancelPaymentResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private companion object {
        const val LOG_TAG = "PaymentFlow: [ServerApi]"
    }
}

