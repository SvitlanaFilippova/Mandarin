package com.mandarinkafe.mandarin.features.more.data.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.more.data.dto.FeedbackRequest
import com.mandarinkafe.mandarin.features.more.data.network.FeedbackServerApi
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Result

class FeedbackRepositoryImpl(
    private val feedbackServerApi: FeedbackServerApi,
    private val authRepository: AuthRepository,
) : FeedbackRepository {

    override suspend fun sendFeedback(feedback: Feedback): Result<Unit> {
        return try {
            val accessToken = authRepository.getAccessToken()
                ?: return Result.Failure(IllegalStateException("Требуется авторизация"))

            val response = feedbackServerApi.sendFeedback(
                token = "$BEARER_TOKEN_TYPE $accessToken",
                request = FeedbackRequest(
                    message = feedback.message,
                    needAnswer = feedback.needAnswer,
                ),
            )

            if (response.resultCode == HTTP_SUCCESS) {
                Result.Success(Unit)
            } else {
                Result.Failure(IllegalStateException(mapError(response.resultCode)))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    private fun mapError(resultCode: Int): String =
        when (resultCode) {
            HTTP_TOO_MANY_REQUESTS -> "Слишком много сообщений. Попробуйте позже."
            HTTP_UNAUTHORIZED -> "Нужно авторизоваться заново."
            else -> "Не удалось отправить сообщение."
        }

    companion object {
        private const val HTTP_UNAUTHORIZED = 401
        private const val HTTP_TOO_MANY_REQUESTS = 429
    }
}
