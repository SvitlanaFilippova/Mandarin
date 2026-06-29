package com.mandarinkafe.mandarin.features.more.data.network

import com.mandarinkafe.mandarin.BuildKonfig
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.more.data.dto.FeedbackRequest
import com.mandarinkafe.mandarin.util.Constants.HEADER_API_KEY
import com.mandarinkafe.mandarin.util.Constants.HEADER_AUTHORIZATION
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class FeedbackServerApi(
    private val client: HttpClient,
) {
    private val key = BuildKonfig.MANDARIN_API_KEY

    suspend fun sendFeedback(token: String, request: FeedbackRequest): Response {
        return try {
            val httpResponse = client.post(FEEDBACK_ENDPOINT) {
                header(HEADER_API_KEY, key)
                header(HEADER_AUTHORIZATION, token)
                setBody(request)
            }

            when (httpResponse.status) {
                HttpStatusCode.Created -> Response().apply { resultCode = HTTP_SUCCESS }
                HttpStatusCode.Unauthorized -> Response().apply {
                    resultCode = HttpStatusCode.Unauthorized.value
                }

                HttpStatusCode.TooManyRequests -> Response().apply {
                    resultCode = HttpStatusCode.TooManyRequests.value
                }

                else -> Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        } catch (e: Throwable) {
            Napier.e("$LOG_TAG.sendFeedback: Exception", e)
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private companion object {
        const val FEEDBACK_ENDPOINT = "/feedback"
        const val LOG_TAG = "FeedbackServerApi"
    }
}
