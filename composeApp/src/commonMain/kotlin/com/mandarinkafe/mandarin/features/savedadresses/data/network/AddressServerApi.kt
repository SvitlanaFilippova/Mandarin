package com.mandarinkafe.mandarin.features.savedadresses.data.network

import com.mandarinkafe.mandarin.BuildKonfig
import com.mandarinkafe.mandarin.core.data.dto.Response
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

class AddressServerApi(private val client: HttpClient) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun getAddresses(token: String): AddressResponse {
        return try {
            val httpResponse = client.get("/addresses") {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val responseBody: AddressResponse = httpResponse.body()
                    responseBody.apply { resultCode = HTTP_SUCCESS }
                }

                HttpStatusCode.Unauthorized -> {
                    AddressResponse().apply { resultCode = HttpStatusCode.Unauthorized.value }
                }

                else -> {
                    Napier.e("AddressServerApi: getAddresses - HTTP error ${httpResponse.status.value}")
                    AddressResponse().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AddressServerApi: getAddresses - Exception", e)
            AddressResponse().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun createOrUpdateAddress(token: String, body: AddressUpdateRequest): Response {
        return try {
            val httpResponse = client.post("/addresses") {
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

                else -> {
                    Napier.e("AddressServerApi: createOrUpdateAddress - HTTP error ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AddressServerApi: createOrUpdateAddress - Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    suspend fun deleteAddress(token: String, addressId: String): Response {
        return try {
            val httpResponse = client.delete("/addresses/$addressId") {
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
                    Napier.e("AddressServerApi: deleteAddress - HTTP error ${httpResponse.status.value}")
                    Response().apply { resultCode = HTTP_SERVER_ERROR }
                }
            }
        } catch (e: Throwable) {
            Napier.e("AddressServerApi: deleteAddress - Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }
}

